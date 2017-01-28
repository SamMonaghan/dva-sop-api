package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.exceptions.DvaSopApiError;
import au.gov.dva.sopapi.interfaces.InstrumentChangeFactory;
import au.gov.dva.sopapi.interfaces.RegisterClient;
import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.SoPLoader;
import au.gov.dva.sopapi.interfaces.model.SopChange;
import au.gov.dva.sopapi.sopref.data.FederalRegisterOfLegislationClient;
import au.gov.dva.sopapi.sopref.parsing.factories.ServiceLocator;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
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
        }
        catch (DvaSopApiError e)
        {
            logger.error("Error occurred when patching repository.", e);
        }
    }

    public static void updateChangeList(Repository repository, InstrumentChangeFactory newInstrumentFactory, InstrumentChangeFactory updatedInstrumentFactory) {

        try {
            ImmutableSet<SopChange> newInstruments = newInstrumentFactory.getChanges();
            ImmutableSet<SopChange> updatedInstruments = updatedInstrumentFactory.getChanges();
            ImmutableSet<SopChange> allNewChanges = new ImmutableSet.Builder<SopChange>()
                    .addAll(newInstruments)
                    .addAll(updatedInstruments)
                    .build();

            ImmutableSet<SopChange> existingChanges = repository.getInstrumentChanges();

            ImmutableSet<SopChange> newChangesNotInRepository = Sets.difference(allNewChanges, existingChanges).immutableCopy();

            repository.addInstrumentChanges(newChangesNotInRepository);
            repository.setLastUpdated(OffsetDateTime.now());
        } catch (DvaSopApiError e) {
            logger.error("Error occurred when updating change list.", e);
        }

    }

    public static void updateServiceDeterminations(Repository repository, RegisterClient registerClient)
    {
        LegRegChangeDetector legRegChangeDetector = new LegRegChangeDetector(registerClient);

        ImmutableSet<String> currentServiceDeterminationIds = repository.getServiceDeterminations().stream()
                .map(sd -> sd.getRegisterId())
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableSet::copyOf));



    }

}




