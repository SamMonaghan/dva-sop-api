package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;

public class NewInstrument implements InstrumentChange {

    public final String instrumentId;
    public final LocalDate date;

    public NewInstrument(String instrumentId, LocalDate date) {
        this.instrumentId = instrumentId;
        this.date = date;
    }

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
        return null;
    }

    @Override
    public void Apply(Repository repository) {
    }
}
