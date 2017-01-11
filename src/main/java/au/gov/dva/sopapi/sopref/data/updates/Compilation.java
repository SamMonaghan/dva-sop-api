package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.exceptions.AutoUpdateError;
import au.gov.dva.sopapi.interfaces.JsonSerializable;
import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import au.gov.dva.sopapi.interfaces.model.InstrumentChangeBase;
import au.gov.dva.sopapi.interfaces.model.SoP;
import au.gov.dva.sopapi.sopref.data.sops.StoredSop;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.function.Function;

public class Compilation extends InstrumentChangeBase implements InstrumentChange, JsonSerializable {


    private final String oldRegisterId;

    @Override
    public String toString() {
        return "Compilation{" +
                "oldRegisterId='" + oldRegisterId + '\'' +
                "} " + super.toString();
    }

    public Compilation(String currentRegisterId, OffsetDateTime date, String oldRegisterId) {
        super(currentRegisterId, date);
        this.oldRegisterId = oldRegisterId;
    }

    public static final String TYPE_NAME = "compilation";



    @Override
    public String getInstrumentId() {
        return super.getInstrumentId();
    }

    @Override
    public OffsetDateTime getDate() {
        return super.getDate();
    }

    @Override
    public void apply(Repository repository, Function<String, Optional<SoP>> soPProvider) {

        Optional<SoP> existing = repository.getSop(getInstrumentId());
        if (existing.isPresent())
            return;

        Optional<SoP> toEndDate = repository.getSop(oldRegisterId);
        if (!toEndDate.isPresent())
        {
            throw new AutoUpdateError(String.format("Attempt to update the end date of SoP %s failed because it is not present in the Repository.", oldRegisterId));
        }

        Optional<SoP> newCompilation = soPProvider.apply(getInstrumentId());
        if (!newCompilation.isPresent())
        {
            throw new AutoUpdateError(String.format("Could not get new compilation for SoP: %s", getInstrumentId()));
        }

        SoP endDatedSop = StoredSop.withEndDate(toEndDate.get(), newCompilation.get().getEffectiveFromDate().minusDays(1));
        repository.archiveSoP(oldRegisterId);
        repository.saveSop(endDatedSop);
        repository.saveSop(newCompilation.get());
    }

    @Override
    public JsonNode toJson() {
        return null;
    }
}
