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

// /Latest route
public class Compilation extends InstrumentChangeBase implements InstrumentChange, JsonSerializable {


    private final String oldRegisterId;

    public Compilation(String registerId, OffsetDateTime date, String oldRegisterId) {
        super(registerId, date);
        this.oldRegisterId = oldRegisterId;
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
    public void Apply(Repository repository, Function<String, Optional<SoP>> soPProvider) {


        // todo WIP
        // remove old register id
        // add new

        repository.deleteSoPIfExists(oldRegisterId);
//        repository.saveSop(soPProvider.apply(getInstrumentId()).);

    }



    @Override
    public JsonNode toJson() {
        return null;
    }
}
