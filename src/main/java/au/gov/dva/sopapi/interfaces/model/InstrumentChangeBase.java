package au.gov.dva.sopapi.interfaces.model;

import au.gov.dva.sopapi.DateTimeUtils;
import au.gov.dva.sopapi.exceptions.AutoUpdateError;
import au.gov.dva.sopapi.sopref.data.updates.NewInstrument;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InstrumentChangeBase {
   protected static final String TYPE_LABEL = "type";
   protected static final String DATE_LABEL = "date";
   protected static final String INSTRUMENT_ID_LABEL = "registerId";

   protected ObjectNode getCommonNode(String typeName, String instrumentId, LocalDate date)
   {
      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode objectNode = objectMapper.createObjectNode();
      objectNode.put(TYPE_LABEL,typeName);
      objectNode.put(INSTRUMENT_ID_LABEL,instrumentId);
      objectNode.put(DATE_LABEL, DateTimeUtils.localDateToUtcLocalDate(date));
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
       return jsonNode.findValue(TYPE_LABEL).asText();
   }

   protected static LocalDate extractDate(JsonNode jsonNode)
   {
       String dateText = jsonNode.findValue(DATE_LABEL).asText();
       LocalDate date = LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE);
       return date;
   }

}
