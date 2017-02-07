package au.gov.dva.dvasopapi.tests.mocks.processingRules.dvatesting;

import au.gov.dva.dvasopapi.tests.ResourceDirectoryLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class DvaDefinedTests {


    @Parameterized.Parameters
    public static Collection<Object[]> data() throws IOException {

        // load all json file names
        // get expected result and convert to boolean

        ResourceDirectoryLoader resourceDirectoryLoader = new ResourceDirectoryLoader();
        List<String> testFileNames =  resourceDirectoryLoader.getResourceFiles("dvaDefinedTests");
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
    public void runDvaDefinedStpTests()
    {
            Integer x = 10;
            
    }

}
