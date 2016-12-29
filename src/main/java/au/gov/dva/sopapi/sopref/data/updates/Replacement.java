package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;

import java.time.LocalDate;

// A SoP is repealed and replaced with one with the same name.
// Shows in the repealed by area of Legislation Register.
public class Replacement implements InstrumentChange {
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
