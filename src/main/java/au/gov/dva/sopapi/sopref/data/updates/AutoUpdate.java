package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.interfaces.InstrumentChangeFactory;
import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.SoPLoader;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoUpdate {

    private static Logger logger = LoggerFactory.getLogger(AutoUpdate.class);

    public static void patchChanges(SoPLoader soPLoader) {

        soPLoader.applyAll(30);
    }


    public static void updateChangeList(Repository repository, InstrumentChangeFactory newInstrumentFactory, InstrumentChangeFactory updatedInstrumentFactory)
    {

        ImmutableSet<InstrumentChange> newInstruments = newInstrumentFactory.getChanges();
        ImmutableSet<InstrumentChange> updatedInstruments = updatedInstrumentFactory.getChanges();
        ImmutableSet<InstrumentChange> allNewChanges = new ImmutableSet.Builder<InstrumentChange>()
                .addAll(newInstruments)
                .addAll(updatedInstruments)
                .build();

        ImmutableSet<InstrumentChange> existingChanges = repository.getInstrumentChanges();

        ImmutableSet<InstrumentChange> newChangesNotInRepository = Sets.difference(allNewChanges, existingChanges).immutableCopy();

        repository.addInstrumentChanges(newChangesNotInRepository);

    }



}
