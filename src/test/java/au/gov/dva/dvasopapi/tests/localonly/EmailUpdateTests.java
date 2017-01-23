package au.gov.dva.dvasopapi.tests.localonly;

import au.gov.dva.dvasopapi.tests.categories.IntegrationTest;
import au.gov.dva.sopapi.interfaces.model.LegislationRegisterEmailUpdate;
import au.gov.dva.sopapi.sopref.data.updates.LegislationRegisterEmailUpdates;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EmailUpdateTests {

    @Test
    @Category(IntegrationTest.class)
    public void testEmailClient() throws ExecutionException, InterruptedException {
        // manual setup:
        // set "LRS_USERID" and "LRS_PASSWORD" environment variables or jvm args
        // set sender below
        // send emails from that sender

        String emailSender = "nick.miller@govlawtech.com.au";
        OffsetDateTime aLongTimeAgo = OffsetDateTime.now().minusDays(100);
        CompletableFuture<ImmutableSet<LegislationRegisterEmailUpdate>> resultFromEmailsForwardedByNm =
            LegislationRegisterEmailUpdates.getEmailsReceivedBetween(aLongTimeAgo, OffsetDateTime.now(),emailSender);
        Assert.assertTrue(resultFromEmailsForwardedByNm.get().size() > 0);
        resultFromEmailsForwardedByNm.get().stream().forEach(r -> System.out.println(r));

        emailSender = "chrisflemming@gmail.com";
        CompletableFuture<ImmutableSet<LegislationRegisterEmailUpdate>> resultFromEmailsForwardedByCf =
                LegislationRegisterEmailUpdates.getEmailsReceivedBetween(aLongTimeAgo,OffsetDateTime.now(), emailSender);

        Assert.assertTrue(resultFromEmailsForwardedByNm.get().size() > 0);
        resultFromEmailsForwardedByNm.get().stream().forEach(r -> System.out.println(r));



    }

}
