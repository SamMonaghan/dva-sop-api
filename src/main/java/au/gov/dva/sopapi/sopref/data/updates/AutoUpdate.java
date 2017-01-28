package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.exceptions.AutoUpdateError;
import au.gov.dva.sopapi.exceptions.DvaSopApiError;
import au.gov.dva.sopapi.interfaces.InstrumentChangeFactory;
import au.gov.dva.sopapi.interfaces.RegisterClient;
import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.SoPLoader;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import au.gov.dva.sopapi.interfaces.model.ServiceDetermination;
import au.gov.dva.sopapi.sopref.data.Conversions;
import au.gov.dva.sopapi.sopref.data.FederalRegisterOfLegislationClient;
import au.gov.dva.sopapi.sopref.parsing.ServiceDeterminationsParser;
import au.gov.dva.sopapi.sopref.parsing.factories.ServiceLocator;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class AutoUpdate {

    private static Logger logger = LoggerFactory.getLogger(AutoUpdate.class);

    public static void patchChanges(Repository repository) {

        try {
            SoPLoader soPLoader = new SoPLoaderImpl(
                    repository,
                    new FederalRegisterOfLegislationClient(),
                    s -> ServiceLocator.findTextCleanser(s),
                    s -> ServiceLocator.findSoPFactory(s)
            );
            soPLoader.applyAll(60);
        } catch (DvaSopApiError e) {
            logger.error("Error occurred when patching repository.", e);
        }
    }

    public static void updateChangeList(Repository repository, InstrumentChangeFactory newInstrumentFactory, InstrumentChangeFactory updatedInstrumentFactory) {

        try {
            ImmutableSet<InstrumentChange> newInstruments = newInstrumentFactory.getChanges();
            ImmutableSet<InstrumentChange> updatedInstruments = updatedInstrumentFactory.getChanges();
            ImmutableSet<InstrumentChange> allNewChanges = new ImmutableSet.Builder<InstrumentChange>()
                    .addAll(newInstruments)
                    .addAll(updatedInstruments)
                    .build();

            ImmutableSet<InstrumentChange> existingChanges = repository.getInstrumentChanges();

            ImmutableSet<InstrumentChange> newChangesNotInRepository = Sets.difference(allNewChanges, existingChanges).immutableCopy();

            repository.addInstrumentChanges(newChangesNotInRepository);
            repository.setLastUpdated(OffsetDateTime.now());
        } catch (DvaSopApiError e) {
            logger.error("Error occurred when updating change list.", e);
        }

    }

    public static void updateServiceDeterminations(Repository repository, RegisterClient registerClient) {
        LegRegChangeDetector legRegChangeDetector = new LegRegChangeDetector(registerClient);

        ImmutableSet<String> currentServiceDeterminationIds = repository.getServiceDeterminations().stream()
                .map(sd -> sd.getRegisterId())
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableSet::copyOf));

        ImmutableSet<InstrumentChange> replacements = legRegChangeDetector.detectReplacements(currentServiceDeterminationIds);
        replacements.forEach(r -> {

            try {
                // todo: could run these two below in parallel
                byte[] docx = registerClient.getLatestDocxInstrument(r.getTargetInstrumentId()).get(30, TimeUnit.SECONDS);
                byte[] pdf = registerClient.getLatestAuthorisedInstrumentPdf(r.getTargetInstrumentId()).get(30, TimeUnit.SECONDS);

                String plainText = Conversions.pdfToPlainText(pdf);
                ServiceDetermination serviceDetermination = ServiceDeterminationsParser.createServiceDetermination(docx,plainText);
                repository.archiveServiceDetermination(r.getSourceInstrumentId());
                repository.addServiceDetermination(serviceDetermination);

            } catch (InterruptedException e) {
                throw new AutoUpdateError(e);
            } catch (ExecutionException e) {
                throw new AutoUpdateError(e);
            } catch (TimeoutException e) {
                throw new AutoUpdateError(e);
            } catch (IOException e) {

                throw new AutoUpdateError(e);
            }




        });
    }





}




