package au.gov.dva.sopapi.sopref.data.updates.changefactories;

import au.gov.dva.sopapi.interfaces.InstrumentChangeFactory;
import au.gov.dva.sopapi.interfaces.RegisterClient;
import au.gov.dva.sopapi.interfaces.model.SopChange;
import au.gov.dva.sopapi.sopref.data.updates.LegRegChangeDetector;
import com.google.common.collect.ImmutableSet;

import java.util.function.Supplier;

public class LegislationRegisterSiteChangeFactory implements InstrumentChangeFactory {

    private final LegRegChangeDetector legRegChangeDetector;
    private final Supplier<ImmutableSet<String>> getExistingInstrumentIds;

    public LegislationRegisterSiteChangeFactory(RegisterClient registerClient,  Supplier<ImmutableSet<String>> getExistingInstrumentIds)
    {
        legRegChangeDetector = new LegRegChangeDetector(registerClient);
        this.getExistingInstrumentIds = getExistingInstrumentIds;
    }

    @Override
    public ImmutableSet<SopChange> getChanges() {
        ImmutableSet<String> currentRegisterIds = getExistingInstrumentIds.get();
        ImmutableSet<SopChange> newReplacements = legRegChangeDetector.detectReplacements(currentRegisterIds);
        ImmutableSet<SopChange> newCompilations = legRegChangeDetector.detectNewCompilations(currentRegisterIds);
        return new ImmutableSet.Builder<SopChange>().addAll(newReplacements).addAll(newCompilations).build();

    }
}
