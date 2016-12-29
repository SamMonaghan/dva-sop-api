package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;

import java.time.LocalDate;

public class RepealWithoutReplacement implements InstrumentChange {
    @Override
    public String getInstrumentId() {
        return null;
    }

    @Override
    public LocalDate getDate() {
        return null;
    }

    @Override
    public void Apply(Repository repository) {

    }
}
