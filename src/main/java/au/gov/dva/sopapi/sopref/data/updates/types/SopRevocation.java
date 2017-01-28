package au.gov.dva.sopapi.sopref.data.updates.types;

import au.gov.dva.sopapi.interfaces.JsonSerializable;
import au.gov.dva.sopapi.interfaces.model.InstrumentChangeBase;
import au.gov.dva.sopapi.interfaces.model.SopChange;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class SopRevocation extends InstrumentChangeBase implements SopChange, JsonSerializable {
    private final LocalDate revocationDate;

    @Override
    public String toString() {
        return "Revocation{" +
                "revocationDate=" + revocationDate +
                "} " + super.toString();
    }

    public SopRevocation(String registerId, OffsetDateTime date, LocalDate revocationDate) {
        super(registerId, registerId, date);
        this.revocationDate = revocationDate;
    }

    public static final String TYPE_NAME = "revocation";

    public LocalDate getRevocationDate(){
        return revocationDate;
    }


    @Override
    public OffsetDateTime getDate() {
        return super.getDate();
    }

    @Override
    public String getSourceInstrumentId() {
        return super.getSourceRegisterId();
    }

    @Override
    public String getTargetInstrumentId() {
        return super.getTargetRegisterId();
    }


    private static final String REVOCATION_DATE = "revocationDate";
    private static final String REGISTER_ID = "registerId";

    @Override
    public JsonNode toJson() {
        ObjectNode root = getCommonNode(TYPE_NAME,getDate());
        root.put(REVOCATION_DATE,revocationDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        root.put(REGISTER_ID,getSourceInstrumentId());
        return root;
    }

    public static SopRevocation fromJson(JsonNode jsonNode)
    {
        String repealDateString =  jsonNode.findValue(REVOCATION_DATE).asText();
        LocalDate repealDate = LocalDate.parse(repealDateString,DateTimeFormatter.ISO_LOCAL_DATE);
        return new SopRevocation(jsonNode.findValue(REGISTER_ID).asText(),extractDate(jsonNode), repealDate);
    }
}
