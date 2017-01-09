package au.gov.dva.sopapi.interfaces.model;

import au.gov.dva.sopapi.exceptions.AutoUpdateError;
import au.gov.dva.sopapi.sopref.data.updates.NewInstrument;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class InstrumentChangeBase {
   @Override
   public String toString() {
      return "InstrumentChangeBase{" +
              "registerId='" + registerId + '\'' +
              ", date=" + date +
              '}';
   }

   protected static final String TYPE_LABEL = "type";
   protected static final String DATE_LABEL = "date";
   protected static final String INSTRUMENT_ID_LABEL = "registerId";
   private final String registerId;
   private final OffsetDateTime date;

   protected InstrumentChangeBase(String registerId, OffsetDateTime date)
   {

      this.registerId = registerId;
      this.date = date;
   }



   protected ObjectNode getCommonNode(String typeName, String instrumentId, OffsetDateTime date)
   {
      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode objectNode = objectMapper.createObjectNode();
      objectNode.put(TYPE_LABEL,typeName);
      objectNode.put(INSTRUMENT_ID_LABEL,instrumentId);
      objectNode.put(DATE_LABEL, date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
      return objectNode;
   }

   public static InstrumentChange fromJson(JsonNode jsonNode)
   {
      String type = jsonNode.findValue(TYPE_LABEL).asText();
      assert (type != null && !type.isEmpty());
      switch (type) {
          case NewInstrument.TYPE_NAME: return NewInstrument.fromJson(jsonNode);
          default: throw new AutoUpdateError(String.format("Cannot deserialize this type of instrument change from JSON: %s", type));
      }
   }

   protected static String extractInstrumentId(JsonNode jsonNode)
   {
       return jsonNode.findValue(INSTRUMENT_ID_LABEL).asText();
   }

   protected static OffsetDateTime extractDate(JsonNode jsonNode)
   {
       String dateText = jsonNode.findValue(DATE_LABEL).asText();
       OffsetDateTime date = OffsetDateTime.parse(dateText, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
       return date;
   }

   public String getInstrumentId() {
      return registerId;
   }

   public OffsetDateTime getDate() {
      return date;
   }
}
