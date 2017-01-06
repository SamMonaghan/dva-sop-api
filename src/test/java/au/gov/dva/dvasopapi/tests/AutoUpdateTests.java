package au.gov.dva.dvasopapi.tests;

import au.gov.dva.sopapi.DateTimeUtils;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import au.gov.dva.sopapi.interfaces.model.InstrumentChangeBase;
import au.gov.dva.sopapi.sopref.data.JsonUtils;
import au.gov.dva.sopapi.sopref.data.updates.NewInstrument;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import scala.util.parsing.json.JSON;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.*;

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

    @Ignore
    @Test
    public void createInitialNewInstrumentsJson() throws IOException {
        String[] rhList = Resources.toString(Resources.getResource("rhSopRegisterIds.txt"),Charsets.UTF_8).split("[\r\n]+");
        String[] bopList = Resources.toString(Resources.getResource("bopSopRegisterIds.txt"),Charsets.UTF_8).split("[\r\n]+");
        Assert.assertTrue(rhList.length == 52);
        Assert.assertTrue(bopList.length == 52);
        ImmutableList<String> rh = copyOf(rhList);
        ImmutableList<String> bop = copyOf(bopList);
        ImmutableList<String> both =  new ImmutableList.Builder<String>().addAll(rh).addAll(bop).build();
        OffsetDateTime creationDate = DateTimeUtils.localDateToMidnightACTDate(LocalDate.of(2017,1,6));
        Stream<JsonNode> instrumentChangeStream = both.stream()
                .p(id -> new NewInstrument(id,creationDate))
                .map(ni -> ni.toJson());

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        instrumentChangeStream.forEach(jsonNode -> arrayNode.add(jsonNode));
        System.out.println(TestUtils.prettyPrint(arrayNode));

    }






}
