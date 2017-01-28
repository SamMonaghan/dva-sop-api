package au.gov.dva.sopapi.sopref.data.updates.types;

import au.gov.dva.sopapi.interfaces.JsonSerializable;
import au.gov.dva.sopapi.interfaces.model.InstrumentChangeBase;
import au.gov.dva.sopapi.interfaces.model.SopChange;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.OffsetDateTime;

public class NewSop extends InstrumentChangeBase implements SopChange, JsonSerializable {


    public NewSop(String registerId, OffsetDateTime date) {
        super(registerId, registerId, date);
    }



    public static final String TYPE_NAME = "new";


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

    public JsonNode toJson()
    {

        ObjectNode root =  getCommonNode(TYPE_NAME,getDate());
        root.put("registerId",super.getSourceRegisterId());
        return root;
    }




    @Override
    public String toString() {
        return "NewInstrument{} " + super.toString();
    }

    public static NewSop fromJson(JsonNode jsonNode)
    {
        return new NewSop(
                jsonNode.findValue("registerId").asText(),extractDate(jsonNode));
    }

}
