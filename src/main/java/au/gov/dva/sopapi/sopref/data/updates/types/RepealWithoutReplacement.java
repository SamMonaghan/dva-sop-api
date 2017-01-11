package au.gov.dva.sopapi.sopref.data.updates.types;

import au.gov.dva.sopapi.interfaces.JsonSerializable;
import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import au.gov.dva.sopapi.interfaces.model.InstrumentChangeBase;
import au.gov.dva.sopapi.interfaces.model.SoP;
import au.gov.dva.sopapi.sopref.data.sops.StoredSop;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.function.Function;

public class RepealWithoutReplacement extends InstrumentChangeBase implements InstrumentChange, JsonSerializable {
    private final LocalDate repealDate;

    public RepealWithoutReplacement(String registerId, OffsetDateTime date, LocalDate repealDate) {
        super(registerId, date);
        this.repealDate = repealDate;
    }

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
        if (!existing.isPresent())
            return;

        repository.archiveSoP(getInstrumentId());
        SoP endDated = StoredSop.withEndDate(existing.get(),repealDate);
        repository.saveSop(endDated);
    }


    @Override
    public JsonNode toJson() {
        return null;
    }
}
