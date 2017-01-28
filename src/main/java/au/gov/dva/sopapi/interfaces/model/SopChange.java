package au.gov.dva.sopapi.interfaces.model;

import au.gov.dva.sopapi.interfaces.JsonSerializable;

import java.time.OffsetDateTime;

public interface SopChange extends JsonSerializable {
     OffsetDateTime getDate();
     String getSourceInstrumentId();
     String getTargetInstrumentId();
}


