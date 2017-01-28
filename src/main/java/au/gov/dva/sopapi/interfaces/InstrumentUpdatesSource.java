package au.gov.dva.sopapi.interfaces;

import au.gov.dva.sopapi.interfaces.model.SopChange;

import java.time.LocalDate;

public interface InstrumentUpdatesSource {
    Iterable<SopChange> getChangesFrom(LocalDate date);
}
