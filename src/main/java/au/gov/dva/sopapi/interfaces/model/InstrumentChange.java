package au.gov.dva.sopapi.interfaces.model;

import au.gov.dva.sopapi.interfaces.JsonSerializable;
import au.gov.dva.sopapi.interfaces.Repository;

import java.time.LocalDate;

public interface InstrumentChange extends JsonSerializable {
     String getInstrumentId();
     LocalDate getDate();
     void Apply(Repository repository);
}


