package au.gov.dva.sopapi.sopref.data.updates.changefactories;

import au.gov.dva.sopapi.interfaces.InstrumentChangeFactory;
import au.gov.dva.sopapi.interfaces.RegisterClient;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import au.gov.dva.sopapi.sopref.data.updates.SoPChangeDetector;
import au.gov.dva.sopapi.sopref.data.updates.SoPLoader;
import com.google.common.collect.ImmutableSet;

import java.time.OffsetDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

public class LegislationRegisterSiteChangeFactory implements InstrumentChangeFactory {

    private final SoPChangeDetector soPChangeDetector;
    private final RegisterClient registerClient;
    private final Supplier<ImmutableSet<String>> getExistingInstrumentIds;

    public LegislationRegisterSiteChangeFactory(RegisterClient registerClient,  Supplier<ImmutableSet<String>> getExistingInstrumentIds)
    {
        soPChangeDetector = new SoPChangeDetector(registerClient);
        this.registerClient = registerClient;
        this.getExistingInstrumentIds = getExistingInstrumentIds;
    }

    @Override
    public ImmutableSet<InstrumentChange> getChanges() {
        ImmutableSet<String> currentRegisterIds = getExistingInstrumentIds.get();
        ImmutableSet<InstrumentChange> newReplacements = soPChangeDetector.detectReplacements(currentRegisterIds);
        ImmutableSet<InstrumentChange> newCompilations = soPChangeDetector.detectNewCompilations(currentRegisterIds);
        return new ImmutableSet.Builder<InstrumentChange>().addAll(newReplacements).addAll(newCompilations).build();

    }
}
