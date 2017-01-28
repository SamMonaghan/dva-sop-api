package au.gov.dva.sopapi.interfaces;

import au.gov.dva.sopapi.interfaces.model.SopChange;
import com.google.common.collect.ImmutableSet;

public interface InstrumentChangeFactory {
    ImmutableSet<SopChange> getChanges();
}

