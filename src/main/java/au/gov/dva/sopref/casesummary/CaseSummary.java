package au.gov.dva.sopref.casesummary;

import au.gov.dva.sopref.interfaces.model.*;
import au.gov.dva.sopref.interfaces.model.casesummary.CaseSummaryModel;
import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CaseSummary {

    private static CaseSummaryModel _model;
    private static XWPFStyles _styles;

    public static CompletableFuture<byte[]> createCaseSummary(CaseSummaryModel caseSummaryModel) {
        _model = caseSummaryModel;
        return CompletableFuture.supplyAsync(CaseSummary::buildCaseSummary);
    }

    private static byte[] buildCaseSummary() {
        List<XWPFStyle> stylesToImport = getStylesToImport();

        // Set up generated document
        XWPFDocument document = new XWPFDocument();
        document.createStyles();

        // Import styles
        for (XWPFStyle style : stylesToImport) {
            List<XWPFStyle> usedStyles = _styles.getUsedStyleList(style);

            for (XWPFStyle s : usedStyles) {
                document.getStyles().addStyle(s);
            }
        }

        // Create the main sections
        CaseSummarySection documentSection = createDocumentSection();
        CaseSummarySection conditionSection = createConditionSection();
        CaseSummarySection serviceHistorySection = createServiceHistorySection();
        CaseSummarySection sopSection = createSopSection();

        documentSection.add(conditionSection);
        documentSection.add(serviceHistorySection);
        documentSection.add(sopSection);

        // Add all sections to the case summary
        documentSection.addToDocument(document);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            document.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException exception) {
            // TODO: Log exception
        } catch (IOException exception) {
            // TODO: Log exception
        }

        return outputStream.toByteArray();
    }

    private static List<XWPFStyle> getStylesToImport() {
        String templatePath = "docs/Case Summary Template.docx";
        InputStream inputStream = CaseSummary.class.getClassLoader().getResourceAsStream(templatePath);
        List<XWPFStyle> stylesToImport = new ArrayList<XWPFStyle>();

        try (XWPFDocument template = new XWPFDocument(inputStream);) {
            // Get styles from the template document so they can be applied to generated document
            _styles = template.getStyles();
            XWPFStyle heading1Style = _styles.getStyle("Heading1");
            XWPFStyle heading2Style = _styles.getStyle("Heading2");
            XWPFStyle heading3Style = _styles.getStyle("Heading3");
            XWPFStyle heading4Style = _styles.getStyle("Heading4");



            stylesToImport.add(heading1Style);
            stylesToImport.add(heading2Style);
            stylesToImport.add(heading3Style);
            stylesToImport.add(heading4Style);
        } catch (IOException e) {
            // TODO: Log exception
        }

        return stylesToImport;
    }

    private static CaseSummarySection createDocumentSection() {
        CaseSummarySection documentSection = new CaseSummarySection();
        documentSection.add(new CaseSummaryHeading("CASE SUMMARY", "Heading1"));

        return documentSection;
    }

    private static CaseSummarySection createConditionSection() {
        Condition condition = _model.getCondition();

        CaseSummarySection conditionSection = new CaseSummarySection();
        CaseSummarySection conditionData = new CaseSummarySection();

        conditionData.add(new CaseSummaryHeading("CLAIMED CONDITION", "Heading2"));
        String conditionParagraph = "The claimed condition is " + condition.getName() + ".";
        conditionData.add(new CaseSummaryParagraph(conditionParagraph));

        conditionData.add(new CaseSummaryHeading("DATE OF ONSET", "Heading2"));
        String onset = getDatesAsRange(condition.getOnsetStartDate(), condition.getOnsetEndDate());
        String onsetParagraph = "This condition related to an incident dated " + onset + ".";
        conditionData.add(new CaseSummaryParagraph(onsetParagraph));

        conditionSection.add(conditionData);

        return conditionSection;
    }

    private static CaseSummarySection createServiceHistorySection() {
        CaseSummarySection serviceHistorySection = new CaseSummarySection();
        serviceHistorySection.add(new CaseSummaryHeading("SERVICE HISTORY", "Heading2"));

        CaseSummarySection serviceHistoryData = new CaseSummarySection();
        ServiceHistory serviceHistory = _model.getServiceHistory();
        serviceHistoryData.add(new CaseSummaryHeading("DATE OF ENLISTMENT", "Heading3"));
        serviceHistoryData.add(new CaseSummaryParagraph(serviceHistory.getEnlistmentDate().toString()));
        serviceHistoryData.add(new CaseSummaryHeading("ORIGINAL HIRE DATE", "Heading3"));
        serviceHistoryData.add(new CaseSummaryParagraph(serviceHistory.getHireDate().toString()));
        serviceHistoryData.add(new CaseSummaryHeading("DATE OF SEPARATION", "Heading3"));
        serviceHistoryData.add(new CaseSummaryParagraph(serviceHistory.getSeparationDate().toString()));
        serviceHistoryData.add(new CaseSummaryHeading("SERVICES", "Heading3"));

        for (Service service : serviceHistory.getServices()) {
            serviceHistoryData.add(new CaseSummaryHeading("SERVICE", "Heading4"));
            serviceHistoryData.add(new CaseSummaryParagraph(service.getName()));
            serviceHistoryData.add(new CaseSummaryHeading("TYPE", "Heading4"));
            serviceHistoryData.add(new CaseSummaryParagraph(service.getType()));
            serviceHistoryData.add(new CaseSummaryHeading("START DATE", "Heading4"));
            serviceHistoryData.add(new CaseSummaryParagraph(service.getStartDate().toString()));
            serviceHistoryData.add(new CaseSummaryHeading("END DATE", "Heading4"));
            serviceHistoryData.add(new CaseSummaryParagraph(service.getEndDate().toString()));
            serviceHistoryData.add(new CaseSummaryHeading("OPERATIONS", "Heading4"));

            for (Operation operation : service.getOperations()) {
                serviceHistoryData.add(new CaseSummaryHeading("NAME", "Heading4"));
                serviceHistoryData.add((new CaseSummaryParagraph(operation.getName())));
                serviceHistoryData.add(new CaseSummaryHeading("SERVICE TYPE", "Heading4"));
                serviceHistoryData.add((new CaseSummaryParagraph(operation.getServiceType().toString())));
                serviceHistoryData.add(new CaseSummaryHeading("START DATE", "Heading4"));
                serviceHistoryData.add((new CaseSummaryParagraph(operation.getStartDate().toString())));
                serviceHistoryData.add(new CaseSummaryHeading("END DATE", "Heading4"));
                serviceHistoryData.add((new CaseSummaryParagraph(operation.getEndDate().toString())));
            }
        }

        serviceHistorySection.add(serviceHistoryData);

        return serviceHistorySection;
    }

    private static CaseSummarySection createSopSection() {
        CaseSummarySection sopSection = new CaseSummarySection();
        sopSection.add(new CaseSummaryHeading("STATEMENT OF PRINCIPLES", "Heading2"));

        CaseSummarySection sopData = new CaseSummarySection();
        SoP sop = _model.getSop();
        sopData.add(new CaseSummaryHeading("CITATION", "Heading3"));
        sopData.add(new CaseSummaryParagraph(sop.getCitation()));
        sopData.add(new CaseSummaryHeading("STANDARD OF PROOF", "Heading3"));
        sopData.add(new CaseSummaryParagraph(sop.getStandardOfProof().toString()));
        // TODO: Add these properties to the SOP interface?
//        sopData.add(new CaseSummaryHeading("URL", "Heading3"));
//        sopData.add(new CaseSummaryParagraph(sop.getUrl()));

        sopData.add(new CaseSummaryHeading("FACTORS CONNECTED TO SERVICE", "Heading3"));
        for (Factor factor : _model.getFactorsConnectedToService()) {
            sopData.add(new CaseSummaryParagraph(factor.getParagraph() + " " + factor.getText()));
        }

        sopData.add(new CaseSummaryHeading("PROGRESS TOWARDS THRESHOLD", "Heading3"));
        sopData.add(new CaseSummaryParagraph(_model.getThresholdProgress()));

        sopSection.add(sopData);

        return sopSection;
    }

    private static String getDatesAsRange(LocalDate startDate, Optional<LocalDate> endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return endDate.isPresent() ?
                startDate.format(formatter) + " to " + endDate.get().format(formatter) :
                startDate.format(formatter);
    }
}
