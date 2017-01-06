package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.interfaces.RegisterClient;
import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import au.gov.dva.sopapi.interfaces.model.SoP;
import au.gov.dva.sopapi.sopref.data.Conversions;
import au.gov.dva.sopapi.sopref.parsing.traits.SoPCleanser;
import au.gov.dva.sopapi.sopref.parsing.traits.SoPFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;
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

    public void UpdateAll(long timeOutSeconds) {
        ImmutableSet<InstrumentChange> instrumentChanges = repository.getInstrumentChanges();
        Stream<String> instrumentIds = instrumentChanges.stream().map(ic -> ic.getInstrumentId());
        List<CompletableFuture<Optional<SoP>>> updateTasks = instrumentIds.map(id -> createGetSopTask(id)).collect(Collectors.toList());
        CompletableFuture<List<Optional<SoP>>> allTasksAsOneFuture = sequence(updateTasks);
        try {
           List<Optional<SoP>> allSops = allTasksAsOneFuture.get(timeOutSeconds, TimeUnit.MINUTES);
           Stream<SoP> nonEmptySops = allSops.stream().filter(s -> s.isPresent()).map(s -> s.get());
           ImmutableMap<String,SoP> sopMap = toMap(nonEmptySops);

           Function<String,Optional<SoP>> sopProvider = registerId -> {
                if (sopMap.containsKey(registerId))
                    return Optional.of(sopMap.get(registerId));
                else return Optional.empty();
           };

           // todo: could make this asynchronous and batched
           // todo: network timeout, failure handling
           instrumentChanges.forEach(ic -> ic.Apply(repository,sopProvider));

        } catch (InterruptedException e) {
            logger.error("Bulk task to update SoPs was interrupted.",e);
        } catch (ExecutionException e) {
            logger.error("Bulk task to update SoPs failed to execute.",e);
        } catch (TimeoutException e) {
            logger.error(String.format("Bulk task to update SoPs timed out after %d seconds.", timeOutSeconds));
        }
    }

    private ImmutableMap<String,SoP> toMap(Stream<SoP> sops)
    {
        Builder<String,SoP> builder = ImmutableMap.builder();
        sops.forEach(s -> builder.put(s.getRegisterId(),s));
        return builder.build();
    }




    // http://www.nurkiewicz.com/2013/05/java-8-completablefuture-in-action.html
    private static <T> CompletableFuture<List<Optional<T>>> sequence(List<CompletableFuture<Optional<T>>> futures) {
        CompletableFuture<Void> allDoneFuture =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture.thenApply(v ->
                futures.stream().
                        map(future -> future.join()).
                        collect(Collectors.toList())
        );
    }



    public CompletableFuture<Optional<SoP>> createGetSopTask(String registerId) {
        return createGetSopTask(registerId, s -> registerClient.getAuthorisedInstrumentPdf(s), sopCleanserProvider, sopFactoryProvider);
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





