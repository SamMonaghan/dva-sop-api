package au.gov.dva.sopref.data;

import au.gov.dva.sopref.data.SoPs.StoredDefinedTerm;
import au.gov.dva.sopref.interfaces.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class Conversions {



    public static String pdfToPlainText(byte[] pdf) throws IOException {

        RandomAccessBuffer randomAccessBuffer = new RandomAccessBuffer(pdf);
        PDFParser pdfParser = new PDFParser(randomAccessBuffer);
        pdfParser.parse();
        COSDocument cosDocument = pdfParser.getDocument();
        PDDocument pdDocument = new PDDocument(cosDocument);
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String text = pdfTextStripper.getText(pdDocument);
        pdDocument.close();
        randomAccessBuffer.close();
        cosDocument.close();

        return text;
    }

    public static String toString(JsonNode jsonNode) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
    }

}
