package au.gov.dva.dvasopapi.tests;

import au.gov.dva.dvasopapi.tests.categories.IntegrationTest;
import au.gov.dva.sopapi.sopref.data.FederalRegisterOfLegislation;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class LegislationRegisterTests {

    @Test
    public void extractAuthorizedDownloadLinkFromHtml() throws IOException {
        // Source: https://www.legislation.gov.au/Details/F2014L00930/Download
        URL htmlResource = Resources.getResource("lsDownloadPage.html");
        String rawHtml = Resources.toString(htmlResource, Charsets.UTF_8);
        URL result = FederalRegisterOfLegislation.getAuthorisedDocumentLinkFromHtml(rawHtml,"F2014L00930");
        Assert.assertTrue(result.equals(URI.create("https://www.legislation.gov.au/Details/F2014L00930/d88e6f5d-a696-4e2f-8b68-cf8a56acfdd6").toURL()));
    }

    @Category(IntegrationTest.class)
    @Test
    public void directDownloadPdf() throws ExecutionException, InterruptedException, IOException {
        URL testURL = URI.create("https://www.legislation.gov.au/Details/F2014L00930/d88e6f5d-a696-4e2f-8b68-cf8a56acfdd6").toURL();
        byte[] result = new FederalRegisterOfLegislation().downloadFile(testURL).get();
        Assert.assertTrue(result.length == 391904);
    }

    @Category(IntegrationTest.class)
    @Test
    public void getRedirectForDetails() throws MalformedURLException, ExecutionException, InterruptedException {
        URL testInstrumentPage = URI.create("https://www.legislation.gov.au/Latest/F2014L00930/Download").toURL();
        URL result = FederalRegisterOfLegislation.getRedirectTarget(testInstrumentPage).get();
        Assert.assertTrue(result.equals(URI.create("https://www.legislation.gov.au/Details/F2014L00930/Download").toURL()));
    }

    @Category(IntegrationTest.class)
    @Test
    public void endToEndPdfRetrieval() throws ExecutionException, InterruptedException {
        String testRegisterId = "F2014L00930";
        FederalRegisterOfLegislation underTest = new FederalRegisterOfLegislation();
        byte[] result = underTest.getAuthorisedInstrumentPdf(testRegisterId).get();
        Assert.assertTrue(result.length == 391904);
    }

    @Category(IntegrationTest.class)
    @Test
    public void noLongerInForceStatusTextRetrieved() throws IOException {

        // Statement of Principles concerning animal envenomation No. 67 of 2008 (https://www.legislation.gov.au/Series/F2008L03183) was revoked by
        // Statement of Principles concerning animal envenomation (Balance of Probabilities) (No. 82 of 2016) (https://www.legislation.gov.au/Details/F2016L01666)
        // https://www.legislation.gov.au/Series/F2008L03183/RepealedBy lists the repealing SoP, including its RegisterId

        Optional<String> result = FederalRegisterOfLegislation.getStatus(Resources.toString(Resources.getResource("legislationRegister/ceasedNoLongerInForce.html"),Charsets.UTF_8));
        String expectedStatus =  "No longer in force";
        Assert.assertTrue(result.get().contentEquals(expectedStatus));
    }


}
