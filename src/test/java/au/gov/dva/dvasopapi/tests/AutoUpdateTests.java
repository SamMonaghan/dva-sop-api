package au.gov.dva.dvasopapi.tests;

import au.gov.dva.sopapi.DateTimeUtils;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import au.gov.dva.sopapi.interfaces.model.InstrumentChangeBase;
import au.gov.dva.sopapi.sopref.data.JsonUtils;
import au.gov.dva.sopapi.sopref.data.updates.NewInstrument;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Stream;

public class AutoUpdateTests {

    @Test
    public void serializeNewInstrument() throws JsonProcessingException {
        InstrumentChange test = new NewInstrument("F2014L83848", DateTimeUtils.localDateToMidnightACTDate(LocalDate.of(2015,1,1)));
        JsonNode node = test.toJson();
        System.out.print(TestUtils.prettyPrint(node));
        Assert.assertTrue(node != null);
    }

    @Test
    public void deserializeUpdatesList() throws IOException {
        String updatesString = Resources.toString(Resources.getResource("updates/updates.json"), Charsets.UTF_8);
        JsonNode jsonNode = (new ObjectMapper()).readTree(updatesString);
        ImmutableList<JsonNode> jsonObjects = JsonUtils.getChildrenOfArrayNode(jsonNode);
        Stream<InstrumentChange> results = jsonObjects.stream().map(n -> InstrumentChangeBase.fromJson(n));
        Assert.assertTrue(results.count() == 2);
    }
}
