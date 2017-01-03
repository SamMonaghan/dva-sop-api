package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.interfaces.JsonSerializable;
import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import au.gov.dva.sopapi.interfaces.model.InstrumentChangeBase;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;

public class NewInstrument extends InstrumentChangeBase implements InstrumentChange, JsonSerializable {

    private final String instrumentId;
    private final LocalDate date;

    public NewInstrument(String instrumentId, LocalDate date) {
        this.instrumentId = instrumentId;
        this.date = date;
    }

    public static final String TYPE_NAME = "new";

    @Override
    public String getInstrumentId() {
        return instrumentId;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    public JsonNode toJson()
    {
        return getCommonNode(TYPE_NAME,instrumentId,date);
    }

    @Override
    public void Apply(Repository repository) {
    }


    public static NewInstrument fromJson(JsonNode jsonNode)
    {
        return new NewInstrument(extractInstrumentId(jsonNode),extractDate(jsonNode));
    }

}
