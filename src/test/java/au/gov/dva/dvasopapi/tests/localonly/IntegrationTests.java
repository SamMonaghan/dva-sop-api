package au.gov.dva.dvasopapi.tests.localonly;

import au.gov.dva.dvasopapi.tests.TestUtils;
import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.model.SoP;
import au.gov.dva.sopapi.sopref.data.AzureStorageRepository;
import au.gov.dva.sopapi.sopref.data.FederalRegisterOfLegislation;
import au.gov.dva.sopapi.sopref.data.sops.StoredSop;
import au.gov.dva.sopapi.sopref.data.updates.SoPLoader;
import au.gov.dva.sopapi.sopref.parsing.factories.ServiceLocator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class IntegrationTests {

    @Test
    @Category(IntegrationTests.class)
    public void getSoPTaskTest() throws ExecutionException, InterruptedException, JsonProcessingException {

        Repository localRepository = new AzureStorageRepository("UseDevelopmentStorage=true");
        SoPLoader underTest = new SoPLoader(localRepository, new FederalRegisterOfLegislation(),s -> ServiceLocator.findTextCleanser(s),s -> ServiceLocator.findSoPFactory(s));
        CompletableFuture<SoP> result = underTest.createGetSopTask("F2014L00933");
        Assert.assertTrue(result.get() != null);
        SoP sop = result.get();

        System.out.println(TestUtils.prettyPrint(StoredSop.toJson(sop)));

    }
}
