package au.gov.dva.sopapi.interfaces.model;

import au.gov.dva.sopapi.interfaces.JsonSerializable;
import au.gov.dva.sopapi.interfaces.Repository;

import java.time.OffsetDateTime;

public interface InstrumentChange extends JsonSerializable {
     String getInstrumentId();
     OffsetDateTime getDate();
     void Apply(Repository repository);
}


