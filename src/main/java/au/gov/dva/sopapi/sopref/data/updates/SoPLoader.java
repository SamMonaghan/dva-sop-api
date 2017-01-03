package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.exceptions.AutoUpdateError;
import au.gov.dva.sopapi.interfaces.RegisterClient;
import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import au.gov.dva.sopapi.interfaces.model.SoP;
import au.gov.dva.sopapi.sopref.data.Conversions;
import au.gov.dva.sopapi.sopref.parsing.traits.SoPCleanser;
import au.gov.dva.sopapi.sopref.parsing.traits.SoPFactory;
import com.google.common.collect.ImmutableSet;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class SoPLoader {


    private final Repository repository;
    private final RegisterClient registerClient;
    private final Function<String, SoPCleanser> sopCleanserProvider;
    private final Function<String, SoPFactory> sopFactoryProvider;

    public SoPLoader(Repository repository, RegisterClient registerClient, Function<String,SoPCleanser> sopCleanserProvider, Function<String,SoPFactory> sopFactoryProvider)
    {

        this.repository = repository;
        this.registerClient = registerClient;
        this.sopCleanserProvider = sopCleanserProvider;
        this.sopFactoryProvider = sopFactoryProvider;
    }

    private static ImmutableSet<CompletableFuture> CreateUpdateTasks(Repository repository, RegisterClient registerClient) {
        // retrieve updates from repository
        // apply each update

        ImmutableSet<InstrumentChange> instrumentChanges = repository.getInstrumentChanges();

        return null;

    }

    public CompletableFuture<SoP> createGetSopTask(String registerId)
    {
        return createGetSopTask(registerId,s -> registerClient.getAuthorisedInstrumentPdf(s),sopCleanserProvider,sopFactoryProvider);
    }

    private CompletableFuture<SoP> createGetSopTask(String registerId, Function<String,CompletableFuture<byte[]>> authorisedPdfProvider, Function<String,SoPCleanser> sopCleanserProvider, Function<String,SoPFactory> sopFactoryProvider)
    {
        CompletableFuture<SoP> promise = authorisedPdfProvider.apply(registerId).thenApply(bytes ->  {
            try {
                String text = Conversions.pdfToPlainText(bytes);
                SoPCleanser cleanser = sopCleanserProvider.apply(registerId);
                String cleansedText = cleanser.clense(text);
                SoPFactory soPFactory = sopFactoryProvider.apply(registerId);
                SoP soP = soPFactory.create(registerId,cleansedText);
                return soP;

            } catch (IOException e) {
                throw new AutoUpdateError(String.format("Failed to convert PDF to text for register ID: %s", registerId));
            }
        });

        return promise;
    }
}





