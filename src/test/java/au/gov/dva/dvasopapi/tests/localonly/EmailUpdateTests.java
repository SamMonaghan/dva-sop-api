package au.gov.dva.dvasopapi.tests.localonly;

import au.gov.dva.dvasopapi.tests.categories.IntegrationTest;
import au.gov.dva.sopapi.AppSettings;
import au.gov.dva.sopapi.interfaces.model.LegislationRegisterEmailUpdate;
import au.gov.dva.sopapi.sopref.data.updates.LegislationRegisterEmailUpdates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.util.Properties;
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
        OffsetDateTime oneMinuteAgo = OffsetDateTime.now().minusDays(100);
        CompletableFuture<ImmutableSet<LegislationRegisterEmailUpdate>> result =
            LegislationRegisterEmailUpdates.getLatestAfter(oneMinuteAgo,emailSender);
        Assert.assertTrue(result.get().size() > 0);
        result.get().stream().forEach(r -> System.out.println(r));

    }

}
