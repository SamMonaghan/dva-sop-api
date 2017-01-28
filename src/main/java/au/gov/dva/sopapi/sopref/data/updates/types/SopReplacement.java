package au.gov.dva.sopapi.sopref.data.updates.types;

import au.gov.dva.sopapi.interfaces.JsonSerializable;
import au.gov.dva.sopapi.interfaces.model.InstrumentChangeBase;
import au.gov.dva.sopapi.interfaces.model.SopChange;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.OffsetDateTime;

// A SoP is repealed and replaced with one with the same name.
// Shows in the repealed by area of Legislation Register.
public class SopReplacement extends InstrumentChangeBase implements SopChange, JsonSerializable {


    public SopReplacement(String newInstrumentRegisterId, OffsetDateTime date, String oldInstrumentRegisterId) {
        super(oldInstrumentRegisterId, newInstrumentRegisterId, date);
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

    @Override
    public String toString() {
        return "Replacement{} " + super.toString();
    }


    public static final String TYPE_NAME = "replacement";
    private static final String REPEALED_LABEL = "repealedRegisterId";
    private static final String NEW_LABEL = "newRegisterId";

    @Override
    public JsonNode toJson() {
        ObjectNode root = getCommonNode(TYPE_NAME,getDate());
        root.put(REPEALED_LABEL,getSourceInstrumentId());
        root.put(NEW_LABEL,getTargetInstrumentId());
        return root;
    }

    public static SopReplacement fromJson(JsonNode jsonNode)
    {
        return new SopReplacement(jsonNode.findValue(REPEALED_LABEL).asText(), extractDate(jsonNode),jsonNode.findValue(NEW_LABEL).asText());
    }

}
