package au.gov.dva.sopapi.systemtests;

import au.gov.dva.sopapi.AppSettings;
import au.gov.dva.sopapi.client.SoPApiClient;
import au.gov.dva.sopapi.dtos.sopsupport.SopSupportRequestDto;
import au.gov.dva.sopapi.dtos.sopsupport.SopSupportResponseDto;
import au.gov.dva.sopapi.dtos.sopsupport.components.FactorWithInferredResultDto;
import au.gov.dva.sopapi.systemtests.ResourceDirectoryLoader;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class DvaDefinedTest {


    private static final String TEST_FILE_DIR = "dvaDefinedTestData";
    private String fileName;
    private Boolean expectedResult;

    public DvaDefinedTest(String fileName, Boolean expectedResult)
    {
        this.fileName = fileName;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws IOException {

        // load all json file names
        // get expected result and convert to boolean

        ResourceDirectoryLoader resourceDirectoryLoader = new ResourceDirectoryLoader();
        List<String> testFileNames =  resourceDirectoryLoader.getResourceFiles(TEST_FILE_DIR);
        Collection<Object[]> parameters = testFileNames.stream()
                .map(f -> createParameterFromFileName(f))
                .collect(Collectors.toList());
        return parameters;

    }





    private static boolean parsePassOrFail(String fileName){
        if (fileName.toLowerCase().startsWith("pass")){
            return true;
        }
        if (fileName.toLowerCase().startsWith("fail"))
        {
            return false;
        }
        throw new IllegalArgumentException(String.format("File name must start with 'PASS' or 'FAIL': %s.", fileName));
    }

    private static Object[] createParameterFromFileName(String fileName)
    {
        return new Object[]{fileName,parsePassOrFail(fileName)};
    }


    @Test
    public void runDvaDefinedStpTest() throws IOException, ExecutionException, InterruptedException {
        Boolean actualResult = runTestCase(fileName);
        Assert.assertEquals(expectedResult,actualResult);
    }


    private Boolean runTestCase(String jsonFileResourcePath) throws IOException, ExecutionException, InterruptedException {
        String jsonString = Resources.toString(Resources.getResource(TEST_FILE_DIR + "/" + jsonFileResourcePath), Charsets.UTF_8);


        URL url = new URL(AppSettings.getBaseUrl());
        SoPApiClient underTest = new SoPApiClient(url);
        SopSupportResponseDto result = underTest.getSatisfiedFactors(jsonString).get();
        ImmutableList<FactorWithInferredResultDto> satisfiedFactors =
                result.getFactors().stream()
                .filter(f -> f.getSatisfaction())
                .collect(Collectors.collectingAndThen(Collectors.toList(),ImmutableList::copyOf));
        System.out.println("FILE: " + jsonFileResourcePath);
        System.out.println("SATISFIED FACTORS: ");
        if (satisfiedFactors.isEmpty())
        {
            System.out.println("None.");
        }
        else {
            satisfiedFactors.stream().forEach(f -> System.out.printf("* %s %s.%n", f.getParagraph(), f.getText()));
        }
        return !satisfiedFactors.isEmpty();

    }

}
