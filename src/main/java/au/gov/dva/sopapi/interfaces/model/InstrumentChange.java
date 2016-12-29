package au.gov.dva.sopapi.interfaces.model;

import au.gov.dva.sopapi.interfaces.Repository;

import java.time.LocalDate;

public interface InstrumentChange  {
     String getInstrumentId();
     LocalDate getDate();
     void Apply(Repository repository);
}

