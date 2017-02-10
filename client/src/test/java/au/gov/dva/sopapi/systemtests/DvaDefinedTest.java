package au.gov.dva.sopapi.systemtests;

import au.gov.dva.sopapi.AppSettings;
import au.gov.dva.sopapi.client.SoPApiClient;
import au.gov.dva.sopapi.dtos.sopsupport.SopSupportRequestDto;
import au.gov.dva.sopapi.dtos.sopsupport.SopSupportResponseDto;
import au.gov.dva.sopapi.dtos.sopsupport.components.FactorWithInferredResultDto;
import au.gov.dva.sopapi.systemtests.ResourceDirectoryLoader;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import jdk.nashorn.internal.runtime.options.Option;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class DvaDefinedTest {


    private static final String TEST_FILE_DIR = "dvaDefinedTestData";


    public static ImmutableList<String> data() throws IOException {

        ResourceDirectoryLoader resourceDirectoryLoader = new ResourceDirectoryLoader();
        List<String> testFileNames =  resourceDirectoryLoader.getResourceFiles(TEST_FILE_DIR);
        return ImmutableList.copyOf(testFileNames);

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


    @Test
    public void runAllDvaDefinedTests() throws IOException, ExecutionException, InterruptedException {

        ImmutableList<String> testFiles = data();
        if (testFiles.isEmpty())
        {
            System.out.println("No test files found.");
            return;
        }

        List<TestCaseResult> results = testFiles.stream().map(f -> runTestCase(f))
                .collect(Collectors.toList());


        for (TestCaseResult result : results)
        {
            System.out.println(result);
        }

        Assert.assertTrue(results.stream().allMatch(testCaseResult -> testCaseResult.passed));

    }


    public static  TestCaseResult runTestCase(String jsonFileResourcePath)  {
        try {
            String jsonString = Resources.toString(Resources.getResource(TEST_FILE_DIR + "/" + jsonFileResourcePath), Charsets.UTF_8);
            Boolean expectedResult = parsePassOrFail(jsonFileResourcePath);

            URL url = new URL(AppSettings.getBaseUrl());
            SoPApiClient underTest = new SoPApiClient(url);
            SopSupportResponseDto result = underTest.getSatisfiedFactors(jsonString).get();
            ImmutableList<FactorWithInferredResultDto> satisfiedFactors =
                    result.getFactors().stream()
                            .filter(f -> f.getSatisfaction())
                            .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));

            StringBuilder log = new StringBuilder();

            log.append(String.format("SATISFIED FACTORS:%n"));
            if (satisfiedFactors.isEmpty()) {
                System.out.println("None.");
            } else {
                satisfiedFactors.stream().forEach(f -> log.append(String.format("* %s %s.%n", f.getParagraph(), f.getText())));
            }
            Boolean passed = expectedResult == !satisfiedFactors.isEmpty();

            return new TestCaseResult(jsonFileResourcePath,passed,log.toString());

        }
        catch (Exception e) {
            return new TestCaseResult(jsonFileResourcePath,false,e.toString());
        }

    }



}
