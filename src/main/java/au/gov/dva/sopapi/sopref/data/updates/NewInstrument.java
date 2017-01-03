package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.exceptions.AutoUpdateError;
import au.gov.dva.sopapi.interfaces.JsonSerializable;
import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import au.gov.dva.sopapi.interfaces.model.InstrumentChangeBase;
import au.gov.dva.sopapi.interfaces.model.SoP;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.function.Function;

public class NewInstrument extends InstrumentChangeBase implements InstrumentChange, JsonSerializable {

    @Override
    public String toString() {
        return "NewInstrument{" +
                "instrumentId='" + instrumentId + '\'' +
                ", date=" + date +
                '}';
    }

    private final String instrumentId;
    private final OffsetDateTime date;

    public NewInstrument(String instrumentId, OffsetDateTime date) {
        this.instrumentId = instrumentId;
        this.date = date;
    }

    public static final String TYPE_NAME = "new";

    @Override
    public String getInstrumentId() {
        return instrumentId;
    }

    @Override
    public OffsetDateTime getDate() {
        return date;
    }

    public JsonNode toJson()
    {
        return getCommonNode(TYPE_NAME,instrumentId,date);
    }

    @Override
    public void Apply(Repository repository, Function<String,Optional<SoP>> soPProvider)
    {
        Optional<SoP> sop = soPProvider.apply(getInstrumentId());
        if (!sop.isPresent())
        {
            throw new AutoUpdateError(String.format("Cannot get a SoP for instrument ID: %s", getInstrumentId()));
        }
        repository.saveSop(sop.get());
    }


    public static NewInstrument fromJson(JsonNode jsonNode)
    {
        return new NewInstrument(extractInstrumentId(jsonNode),extractDate(jsonNode));
    }

}
