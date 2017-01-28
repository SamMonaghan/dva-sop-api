package au.gov.dva.sopapi.interfaces;

import au.gov.dva.sopapi.interfaces.model.SopChange;
import au.gov.dva.sopapi.interfaces.model.ServiceDetermination;
import au.gov.dva.sopapi.interfaces.model.SoP;
import com.google.common.collect.ImmutableSet;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface Repository {
     void saveSop(SoP sop);
     Optional<SoP> getSop(String registerId);
     ImmutableSet<SoP> getAllSops();
     void archiveSoP(String registerId);
     ImmutableSet<SopChange> getInstrumentChanges();
     void addInstrumentChanges(ImmutableSet<SopChange> sopChanges);
     void addServiceDetermination(ServiceDetermination serviceDetermination);
     ImmutableSet<ServiceDetermination> getServiceDeterminations();
     Optional<OffsetDateTime> getLastUpdated();
     void setLastUpdated(OffsetDateTime offsetDateTime);
}
