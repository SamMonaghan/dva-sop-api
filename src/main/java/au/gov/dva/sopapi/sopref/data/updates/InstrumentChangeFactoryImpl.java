package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.interfaces.InstrumentChangeFactory;
import au.gov.dva.sopapi.interfaces.InstrumentUpdatesSource;
import au.gov.dva.sopapi.interfaces.RegisterClient;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import com.google.common.collect.ImmutableSet;

public class InstrumentChangeFactoryImpl implements InstrumentChangeFactory {
    @Override
    public ImmutableSet<InstrumentChange> createChanges(RegisterClient registerClient, InstrumentUpdatesSource updatesSource) {
        return null;
    }
}
