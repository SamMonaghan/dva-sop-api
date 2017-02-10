package au.gov.dva.sopapi.systemtests;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class FailCase {

    @Test
    public void runSystemTestCaseWhereInsufficientCfts() throws IOException {
        TestCaseResult result = DvaDefinedTest.runTestCase("FAIL_3.json");
        System.out.println(result.log);
        Assert.assertTrue(result.passed);

    }
}
