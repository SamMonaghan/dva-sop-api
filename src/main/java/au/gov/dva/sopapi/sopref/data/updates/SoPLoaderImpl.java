package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.exceptions.AutoUpdateError;
import au.gov.dva.sopapi.exceptions.DvaSopApiError;
import au.gov.dva.sopapi.interfaces.RegisterClient;
import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.SoPLoader;
import au.gov.dva.sopapi.interfaces.model.SoP;
import au.gov.dva.sopapi.interfaces.model.SopChange;
import au.gov.dva.sopapi.sopref.data.Conversions;
import au.gov.dva.sopapi.sopref.data.sops.StoredSop;
import au.gov.dva.sopapi.sopref.data.updates.types.NewSop;
import au.gov.dva.sopapi.sopref.data.updates.types.NewSopCompilation;
import au.gov.dva.sopapi.sopref.data.updates.types.SopReplacement;
import au.gov.dva.sopapi.sopref.data.updates.types.SopRevocation;
import au.gov.dva.sopapi.sopref.parsing.traits.SoPCleanser;
import au.gov.dva.sopapi.sopref.parsing.traits.SoPFactory;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SoPLoaderImpl implements SoPLoader {

    private Repository repository;
    private final RegisterClient registerClient;
    private final Function<String, SoPCleanser> sopCleanserProvider;
    private final Function<String, SoPFactory> sopFactoryProvider;
    private final Logger logger = LoggerFactory.getLogger(SoPLoaderImpl.class);

    public SoPLoaderImpl(Repository repository, RegisterClient registerClient, Function<String, SoPCleanser> sopCleanserProvider, Function<String, SoPFactory> sopFactoryProvider) {
        this.repository = repository;
        this.registerClient = registerClient;
        this.sopCleanserProvider = sopCleanserProvider;
        this.sopFactoryProvider = sopFactoryProvider;
    }

    private Function<String,Optional<SoP>> sopProvider = registerId -> {

        long timeoutSecs = 30;

        CompletableFuture<Optional<SoP>> getSopTask = createGetSopTask(registerId);

        CompletableFuture<Optional<SoP>> resultTask = getSopTask.handle((soP, throwable) -> {
            if (soP == null) {
                logger.error("Exception when running task to get SoP.",throwable);
                return Optional.empty();
            }
            return soP;
        });

        try {
            Optional<SoP> result = resultTask.get(timeoutSecs,TimeUnit.SECONDS);
            return result;

        } catch (InterruptedException e) {
            logger.error(String.format("Task to get SoP was interrupted: %s", registerId),e);
            return Optional.empty();
        } catch (ExecutionException e) {
            logger.error(String.format("Task to get SoP threw execution exception: %s", registerId, e));
            return Optional.empty();
        } catch (TimeoutException e) {
            logger.error(String.format(String.format("Task to get SoP %s timed out after %d seconds.", registerId, timeoutSecs)));
            return Optional.empty();
        }
    };

    private Function<String,ImmutableSet<String>> antecedentRegisterIdProvider = s ->
            repository.getInstrumentChanges()
           .stream()
           .filter(sopChange ->  sopChange instanceof SopReplacement || sopChange instanceof NewSopCompilation)
           .filter(sopChange -> sopChange.getTargetInstrumentId().contentEquals(s))
           .map(SopChange::getSourceInstrumentId)
           .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableSet::copyOf));


    @Override
    public void applyAll(long timeOutSeconds) {
        Stream<SopChange> sequencedInstrumentChanges = repository.getInstrumentChanges()
                .stream()
                .sorted(new InstrumentChangeComparator());

        sequencedInstrumentChanges.forEach(ic -> {
            try {
                applyInstrumentChange(ic,repository,sopProvider);
            }
            catch (DvaSopApiError e)
            {
                logger.error("Failed to apply update to repository for instrument change: " + ic.toString(),e);
            }
        });
    }

    // RR suggested this be decoupled, rather than as methods on the change object.
    private static void applyInstrumentChange(SopChange sopChange, Repository repository, Function<String,Optional<SoP>> soPProvider)
    {
        if (sopChange instanceof NewSop)
        {
            Optional<SoP> existingIfAny = repository.getSop(sopChange.getTargetInstrumentId());
            if (existingIfAny.isPresent())
                return;

            Optional<SoP> sop = soPProvider.apply(sopChange.getTargetInstrumentId());

            if (!sop.isPresent())
            {
                throw new AutoUpdateError(String.format("Cannot get a SoP for instrument ID: %s", sopChange.getTargetInstrumentId()));
            }

            repository.saveSop(sop.get());
        }
        else if (sopChange instanceof SopReplacement)
        {
            Optional<SoP> newInstrument = repository.getSop(sopChange.getTargetInstrumentId());
            if (newInstrument.isPresent())
                return;

            Optional<SoP> toEndDate = repository.getSop(sopChange.getSourceInstrumentId());
            if (!toEndDate.isPresent())
            {
                throw new AutoUpdateError(String.format("Attempt to update the end date of SoP %s failed because it is not present in the Repository.", sopChange.getSourceInstrumentId()));
            }

            Optional<SoP> repealingSop = soPProvider.apply(sopChange.getTargetInstrumentId());
            if (!repealingSop.isPresent())
            {
                throw new AutoUpdateError(String.format("Replacement of repealed SoP %s failed because could not obtain new SoP %s", sopChange.getSourceInstrumentId(), sopChange.getTargetInstrumentId()));
            }

            LocalDate effectiveDateOfNewSoP = repealingSop.get().getEffectiveFromDate();
            SoP endDated = StoredSop.withEndDate(toEndDate.get(), effectiveDateOfNewSoP.minusDays(1));
            repository.saveSop(endDated);
            repository.saveSop(repealingSop.get());
        }

        else if (sopChange instanceof NewSopCompilation)
        {
            Optional<SoP> existing = repository.getSop(sopChange.getTargetInstrumentId());
            if (existing.isPresent())
                return;

            Optional<SoP> toEndDate = repository.getSop(sopChange.getSourceInstrumentId());
            if (!toEndDate.isPresent()) {
                throw new AutoUpdateError(String.format("Attempt to update the end date of SoP %s failed because it is not present in the Repository.", sopChange.getSourceInstrumentId()));
            }

            Optional<SoP> newCompilation = soPProvider.apply(sopChange.getTargetInstrumentId());
            if (!newCompilation.isPresent()) {
                throw new AutoUpdateError(String.format("Could not get new compilation for SoP: %s", sopChange.getTargetInstrumentId()));
            }

            repository.archiveSoP(sopChange.getSourceInstrumentId());
            repository.saveSop(newCompilation.get());
        }

        else if (sopChange instanceof SopRevocation)
        {
            Optional<SoP> existing = repository.getSop(sopChange.getSourceInstrumentId());
            if (!existing.isPresent())
                return;

            repository.archiveSoP(sopChange.getSourceInstrumentId());
            SoP endDated = StoredSop.withEndDate(existing.get(), ((SopRevocation)sopChange).getRevocationDate());
            repository.saveSop(endDated);
        }

        else throw new AutoUpdateError(String.format("Unable to apply this instrument change type to repository: %s", sopChange.getClass().getName()));
    }



    private static class InstrumentChangeComparator implements Comparator<SopChange>, Serializable
    {
        static final long serialVersionUID = 42L;
        @Override
        public int compare(SopChange o1, SopChange o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    }



    public CompletableFuture<Optional<SoP>> createGetSopTask(String registerId) {
        return createGetSopTask(registerId, s -> registerClient.getLatestAuthorisedInstrumentPdf(s), sopCleanserProvider, sopFactoryProvider);
    }

    private CompletableFuture<Optional<SoP>> createGetSopTask(String registerId, Function<String, CompletableFuture<byte[]>> authorisedPdfProvider, Function<String, SoPCleanser> sopCleanserProvider, Function<String, SoPFactory> sopFactoryProvider) {
        CompletableFuture<Optional<SoP>> promise = authorisedPdfProvider.apply(registerId).thenApply(bytes -> {
            String rawText;

            try {
                rawText = Conversions.pdfToPlainText(bytes);
                logger.trace(buildLoggerMessage(registerId, "Successfully converted from PDF to plain text."));
            } catch (IOException ioException) {
                logger.error(buildLoggerMessage(registerId, "Failed to convert from PDF to plain text."), ioException);
                return Optional.empty();
            }

            SoPCleanser cleanser = sopCleanserProvider.apply(registerId);

            String cleansedText;
            try {
                cleansedText = cleanser.cleanse(rawText);
            } catch (Error e) {
                logger.error(buildLoggerMessage(registerId, "Failed to cleanse text."), e);
                return Optional.empty();
            }

            SoPFactory soPFactory = sopFactoryProvider.apply(registerId);
            try {
                SoP soP = soPFactory.create(registerId, rawText, cleansedText);
                return Optional.of(soP);
            } catch (Error e) {

                // try factories for antecedent sops
                ImmutableSet<String> antecedentRegisterIds = antecedentRegisterIdProvider.apply(registerId);

                if (!antecedentRegisterIds.isEmpty()) {
                    for (String antecedent : antecedentRegisterIds) {

                        logger.trace(String.format("Failed to create SoP with register ID %s using default factory,trying factory for antecedent SoP with register ID: %s...", registerId), antecedent);

                        SoPFactory antecedentSopFactory = sopFactoryProvider.apply(antecedent);
                        try {
                            SoP antecedentResult = antecedentSopFactory.create(registerId, rawText, cleansedText);
                            return Optional.of(antecedentResult);
                        }
                        catch (DvaSopApiError antecedentError)
                        {
                            logger.error(String.format("Failed to create SoP with Register ID %s using factory for antecedent SoP with Register ID %s", registerId, antecedent));
                        }

                    }
                }


                logger.error(buildLoggerMessage(registerId, "Failed to create SoP."), e);
                return Optional.empty();
            }
        });

        return promise;
    }

    private static String buildLoggerMessage(String registerId, String message) {
        return String.format("%s: %s", registerId, message);
    }


}





