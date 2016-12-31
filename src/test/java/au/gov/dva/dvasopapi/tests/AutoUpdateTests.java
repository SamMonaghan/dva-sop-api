package au.gov.dva.dvasopapi.tests;

import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import au.gov.dva.sopapi.sopref.data.updates.NewInstrument;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

public class AutoUpdateTests {

    @Test
    public void serializeNewInstrument() throws JsonProcessingException {
        InstrumentChange test = new NewInstrument("F2014L83848", LocalDate.of(2015,1,1));
        JsonNode node = test.toJson();
        System.out.print(TestUtils.prettyPrint(node));
        Assert.assertTrue(node != null);
    }
}
