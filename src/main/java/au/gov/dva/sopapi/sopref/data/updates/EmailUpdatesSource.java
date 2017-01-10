package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.interfaces.InstrumentUpdatesSource;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;

import java.time.LocalDate;

public class EmailUpdatesSource implements InstrumentUpdatesSource {

    @Override
    public Iterable<InstrumentChange> getChangesFrom(LocalDate date) {
        return null;
    }
}
