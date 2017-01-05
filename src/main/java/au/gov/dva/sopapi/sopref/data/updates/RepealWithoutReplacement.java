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

public class RepealWithoutReplacement extends InstrumentChangeBase implements InstrumentChange, JsonSerializable {
    protected RepealWithoutReplacement(String registerId, OffsetDateTime date) {
        super(registerId, date);
    }

    @Override
    public String getInstrumentId() {
        return getInstrumentId();
    }

    @Override
    public OffsetDateTime getDate() {
        return getDate();
    }

    @Override
    public void Apply(Repository repository, Function<String, Optional<SoP>> soPProvider) {
        repository.deleteSoPIfExists(getInstrumentId());
    }


    @Override
    public JsonNode toJson() {
        return null;
    }
}
