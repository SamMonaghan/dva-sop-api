package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.exceptions.DvaSopApiError;

import au.gov.dva.sopapi.interfaces.RegisterClient;
import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import au.gov.dva.sopapi.interfaces.model.SoP;
import au.gov.dva.sopapi.sopref.data.Conversions;
import au.gov.dva.sopapi.sopref.data.updates.types.Compilation;
import au.gov.dva.sopapi.sopref.data.updates.types.NewInstrument;
import au.gov.dva.sopapi.sopref.data.updates.types.Revocation;
import au.gov.dva.sopapi.sopref.parsing.traits.SoPCleanser;
import au.gov.dva.sopapi.sopref.parsing.traits.SoPFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Stream;

public class SoPLoader {

    private final Repository repository;
    private final RegisterClient registerClient;
    private final Function<String, SoPCleanser> sopCleanserProvider;
    private final Function<String, SoPFactory> sopFactoryProvider;
    private final Logger logger = LoggerFactory.getLogger(SoPLoader.class);

    public SoPLoader(Repository repository, RegisterClient registerClient, Function<String, SoPCleanser> sopCleanserProvider, Function<String, SoPFactory> sopFactoryProvider) {
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

    public void applyAll(long timeOutSeconds) {
        Stream<InstrumentChange> sequencedInstrumentChanges = repository.getInstrumentChanges()
                .stream()
                .sorted(new InstrumentChangeComparator());

        sequencedInstrumentChanges.forEach(ic -> {
            try {
                ic.apply(repository, sopProvider);
            }
            catch (DvaSopApiError e)
            {
                logger.error("Failed to apply update to repository for instrument change: " + ic.toString(),e);
            }
        });
    }

    private class InstrumentChangeComparator implements Comparator<InstrumentChange>
    {

        ImmutableMap<String,Integer> ordering = ImmutableMap.of(
                NewInstrument.class.getName(), 1,
                Compilation.class.getName(), 2,
                Revocation.class.getName(),3
        );

        @Override
        public int compare(InstrumentChange o1, InstrumentChange o2) {

            if (ordering.containsKey(o1.getClass().getName()) && ordering.containsKey(o2.getClass().getName()))
            {
                Integer o1Order = ordering.get(o1.getClass().getName());
                Integer o2Order = ordering.get(o2.getClass().getName());

                if (o1Order.equals(o2Order))
                {
                   return o1.getDate().compareTo(o2.getDate());
                }
                else {
                    return (o1Order.compareTo(o2Order));
                }
            }
            else {
                throw new DvaSopApiError(String.format("Do not know how to sequence these types of instrument changes: %s, %s", o1.getClass().getName(), o2.getClass().getName()));
            }
        }

    }

    private ImmutableMap<String,SoP> toMap(Stream<SoP> sops)
    {
        Builder<String,SoP> builder = ImmutableMap.builder();
        sops.forEach(s -> builder.put(s.getRegisterId(),s));
        return builder.build();
    }

    public CompletableFuture<Optional<SoP>> createGetSopTask(String registerId) {
        return createGetSopTask(registerId, s -> registerClient.getLatestAuthorisedInstrumentPdf(s), sopCleanserProvider, sopFactoryProvider);
    }

    private CompletableFuture<Optional<SoP>> createGetSopTask(String registerId, Function<String, CompletableFuture<byte[]>> authorisedPdfProvider, Function<String, SoPCleanser> sopCleanserProvider, Function<String, SoPFactory> sopFactoryProvider) {
        CompletableFuture<Optional<SoP>> promise = authorisedPdfProvider.apply(registerId).thenApply(bytes -> {
            String text;

            try {
                text = Conversions.pdfToPlainText(bytes);
                logger.trace(buildLoggerMessage(registerId, "Successfully converted from PDF to plain text."));
            } catch (IOException ioException) {
                logger.error(buildLoggerMessage(registerId, "Failed to convert from PDF to plain text."), ioException);
                return Optional.empty();
            }

            SoPCleanser cleanser = sopCleanserProvider.apply(registerId);

            String cleansedText;
            try {
                cleansedText = cleanser.clense(text);
            } catch (Error e) {
                logger.error(buildLoggerMessage(registerId, "Failed to cleanse text."), e);
                return Optional.empty();
            }

            SoPFactory soPFactory = sopFactoryProvider.apply(registerId);
            try {
                SoP soP = soPFactory.create(registerId, cleansedText);
                return Optional.of(soP);
            } catch (Error e) {
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





