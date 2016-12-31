package au.gov.dva.sopapi.interfaces.model;

import au.gov.dva.sopapi.DateTimeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDate;

public class InstrumentChangeBase {
   protected String TYPE_LABEL = "type";
   protected String DATE_LABEL = "date";
   protected String INSTRUMENT_ID_LABEL = "registerId";


   protected ObjectNode getCommonNode(String typeName, String instrumentId, LocalDate date)
   {
      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode objectNode = objectMapper.createObjectNode();
      objectNode.put(TYPE_LABEL,typeName);
      objectNode.put(INSTRUMENT_ID_LABEL,instrumentId);
      objectNode.put(DATE_LABEL, DateTimeUtils.localDateToUtcLocalDate(date));
      return objectNode;
   }
}
