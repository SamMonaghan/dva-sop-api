package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import au.gov.dva.sopapi.interfaces.model.SoP;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.function.Function;

// Legislation Register points to latest compilation at /Latest route
public class Amendment implements InstrumentChange {
    @Override
    public String getInstrumentId() {
        return null;
    }

    @Override
    public OffsetDateTime getDate() {
        return null;
    }

    @Override
    public void apply(Repository repository, Function<String, Optional<SoP>> soPProvider) {

    }


    @Override
    public JsonNode toJson() {
        return null;
    }
}