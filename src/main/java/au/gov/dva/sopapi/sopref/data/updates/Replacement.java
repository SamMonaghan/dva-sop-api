package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.interfaces.JsonSerializable;
import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import au.gov.dva.sopapi.interfaces.model.InstrumentChangeBase;
import au.gov.dva.sopapi.interfaces.model.SoP;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.function.Function;

// A SoP is repealed and replaced with one with the same name.
// Shows in the repealed by area of Legislation Register.
public class Replacement extends InstrumentChangeBase implements InstrumentChange, JsonSerializable {
    protected Replacement(String newInstrumentRegisterId, OffsetDateTime date, String oldInstrumentRegisterId) {
        super(newInstrumentRegisterId, date);
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

    }


    @Override
    public JsonNode toJson() {
        return null;
    }
}
