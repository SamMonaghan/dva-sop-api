package au.gov.dva.sopapi.sopref.data.updates.changefactories;

import au.gov.dva.sopapi.interfaces.InstrumentChangeFactory;
import au.gov.dva.sopapi.interfaces.LegislationRegisterEmailClient;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import au.gov.dva.sopapi.interfaces.model.LegislationRegisterEmailUpdate;
import au.gov.dva.sopapi.interfaces.model.SoP;
import com.google.common.collect.ImmutableSet;
import org.apache.log4j.spi.LoggerFactory;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.logging.Logger;

class EmailSubscriptionInstrumentChangeFactory implements InstrumentChangeFactory {

    private final LegislationRegisterEmailClient emailClient;
    private final Supplier<OffsetDateTime> getLastUpdatedDate;
    private Supplier<SoP> getExistingInstruments;
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(EmailSubscriptionInstrumentChangeFactory.class);

    public EmailSubscriptionInstrumentChangeFactory(LegislationRegisterEmailClient emailClient,
                                                    Supplier<OffsetDateTime> getLastUpdatedDate,
                                                    Supplier<SoP> getExistingInstruments) {

        this.emailClient = emailClient;
        this.getLastUpdatedDate = getLastUpdatedDate;
        this.getExistingInstruments = getExistingInstruments;
    }

    private static ImmutableSet<InstrumentChange> identifyNewInstruments(ImmutableSet<SoP> existingInstruments,
                                                                         ImmutableSet<LegislationRegisterEmailUpdate> emailUpdates) {
        return null;
    }

    private static Optional<InstrumentChange> createInstrumentChangeFromEmailUpdate(LegislationRegisterEmailUpdate emailUpdate) {
        Optional<String> registerId = extractRegisterIdFromEmailUrl(emailUpdate.getRegisterLink());

        if (!registerId.isPresent())
            return Optional.empty();

        if (emailUpdate.getUpdateDescription().contentEquals("Item was published"))
        {
            return null;
        }

        return null;
    }

    private static Optional<String> extractRegisterIdFromEmailUrl(URL url)
    {
        String[] pathParts = url.getPath().split("/");
        String lastPart = pathParts[pathParts.length - 1].trim();
        if (lastPart.matches("[A-Z0-9]+"))
        {
            return Optional.of(lastPart);
        }
        logger.error(String.format("Could not extract a Register ID from this link: %s", url.toString()));
        return Optional.empty();
    }



    @Override
    public ImmutableSet<InstrumentChange> getChanges() {
        long timeoutSeconds = 10;
        try {
            ImmutableSet<LegislationRegisterEmailUpdate> updates = emailClient.getUpdatesFrom(getLastUpdatedDate.get()).get(timeoutSeconds, TimeUnit.SECONDS);
            // logic to identify new instruments from legislation email updates here

            return null;
        } catch (InterruptedException e) {
            logger.error("Getting updates from legislation register email subscription was interrupted.",e);
            return ImmutableSet.of();
        } catch (ExecutionException e) {
            logger.error("Getting updates from legislation register email subscription threw execution exception.",e);
            return ImmutableSet.of();
        } catch (TimeoutException e) {
            logger.error(String.format("Getting updates from legislation register email subscription timed out after %d seconds.", timeoutSeconds));
            return ImmutableSet.of();
        }
    }
}

