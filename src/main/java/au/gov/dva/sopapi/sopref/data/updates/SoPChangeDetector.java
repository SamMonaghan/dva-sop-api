package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.interfaces.RegisterClient;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class SoPChangeDetector {

    public SoPChangeDetector(RegisterClient registerClient) {
    }

    public ImmutableSet<InstrumentChange> detectNewCompilations(ImmutableSet<String> registerIds)
    {

    }

    // new instruments - nothing to do
    // check for updated compilation
    // check for repeals
}
