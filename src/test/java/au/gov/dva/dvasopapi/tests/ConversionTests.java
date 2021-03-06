package au.gov.dva.dvasopapi.tests;


import au.gov.dva.dvasopapi.tests.mocks.MockLumbarSpondylosisSopRH;
import au.gov.dva.sopapi.interfaces.model.SoP;
import au.gov.dva.sopapi.sopref.data.Conversions;
import au.gov.dva.sopapi.sopref.data.sops.StoredSop;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;


public class ConversionTests {

    @Test
    public void pdfToText() throws IOException {
        URL inputPdf = Resources.getResource("F2016L00994.pdf");
        byte[] pdfBytes = Resources.toByteArray(inputPdf);
        String result = Conversions.pdfToPlainText(pdfBytes);
        int lineCount = result.split("[\r\n]").length;
        System.out.print(result);
        Assert.assertTrue(lineCount > 250);
    }

    @Test
    public void sopToJson() throws JsonProcessingException {
        SoP testData = new MockLumbarSpondylosisSopRH();
        JsonNode result = StoredSop.toJson(testData);
        System.out.print(TestUtils.prettyPrint(result));
        Assert.assertTrue(result.elements().hasNext());
    }

    @Test
    public void jsonToSop() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(Resources.getResource("storedSop.json"));
        SoP result = StoredSop.fromJson(jsonNode);

        // round trip
        System.out.print(TestUtils.prettyPrint(StoredSop.toJson(result)));
    }

    @Test
    public void jacksonEmptyArray() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree("{ \"key\": [] }");
        JsonNode values = jsonNode.findPath("key");
        Assert.assertTrue(values.size() == 0);
        Assert.assertTrue(values.isArray());

    }

     @Test
    public void jacksonPopulatedArray() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree("{ \"key\": [{\"some\" : \"value\"}] }");
        JsonNode values = jsonNode.findPath("key");
        JsonNode element = values.get(0);

         for (Iterator<JsonNode> it = values.elements(); it.hasNext(); ) {
             JsonNode el = it.next();
             System.out.print(el);
         }
        Assert.assertTrue(values.size() == 1);
        Assert.assertTrue(values.isArray());
        Assert.assertTrue(element.isObject());

    }

    @Test
    public void producecleansedTextForLSBop() throws IOException {

            URL inputPdf = Resources.getResource("sops_bop/F2014L00930.pdf");
            byte[] pdfBytes = Resources.toByteArray(inputPdf);
            String result = Conversions.pdfToPlainText(pdfBytes);
            int lineCount = result.split("[\r\n]+").length;
            System.out.print(result);
            Assert.assertTrue(lineCount > 250);
    }


    @Test
    public void extractExponentInPdf() {
        // todo: make custom PDFTextStripper which detects superscript 2 and replaces it with unicode \u00B2
    }




}
