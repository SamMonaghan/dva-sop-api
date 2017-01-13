package au.gov.dva.sopapi.sopref.data.updates.changefactories;

import au.gov.dva.sopapi.exceptions.AutoUpdateError;
import au.gov.dva.sopapi.interfaces.InstrumentChangeFactory;
import au.gov.dva.sopapi.interfaces.LegislationRegisterEmailClient;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import au.gov.dva.sopapi.interfaces.model.LegislationRegisterEmailUpdate;
import au.gov.dva.sopapi.interfaces.model.SoP;
import au.gov.dva.sopapi.sopref.data.updates.types.NewInstrument;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private static ImmutableSet<InstrumentChange> identifyNewInstruments(ImmutableSet<LegislationRegisterEmailUpdate> emailUpdates) {
        ImmutableSet<InstrumentChange> newInstruments = emailUpdates.stream()
                .filter(u -> u.getUpdateDescription().contains("published"))
                .filter(u -> u.getInstrumentTitle().matches("$Statement of Principles [Cc]oncerning"))
                .filter(u -> !u.getInstrumentTitle().matches("[Aa]mendment"))
                .map(u -> {
                            try {
                                return Optional.of(new NewInstrument(
                                        extractRegisterIdFromEmailUrl(u.getRegisterLink())
                                        , u.getDateReceived()));
                            } catch (AutoUpdateError e) {
                                logger.error("Failed to create new instrument update from email item: %s" + u);
                                return Optional.empty();
                            }
                        }
                    )
                .filter(u -> u.isPresent())
                .map(u -> (InstrumentChange)u.get())
                .collect(Collectors.collectingAndThen(Collectors.toList(),ImmutableSet::copyOf));

        return newInstruments;
    }

    private static String extractRegisterIdFromEmailUrl(URL url)
    {
        String[] pathParts = url.getPath().split("/");
        try {
            String lastPart = pathParts[pathParts.length - 1].trim();
            if (lastPart.matches("[A-Z0-9]+")) {
                return lastPart.trim();
            } else {
                throw new AutoUpdateError(String.format("Could not extract a Register ID from link %s in email item %s", url.toString()));
            }
        }
        catch (Exception e){
            throw new AutoUpdateError(String.format("Could not extract a Register ID from link %s in email item %s", url.toString()),e);
        }
    }


    @Override
    public ImmutableSet<InstrumentChange> getChanges() {
        long timeoutSeconds = 10;
        try {
            ImmutableSet<LegislationRegisterEmailUpdate> updates = emailClient.getUpdatesFrom(getLastUpdatedDate.get()).get(timeoutSeconds, TimeUnit.SECONDS);
           ImmutableSet<InstrumentChange> newInstruments = identifyNewInstruments(updates);
           return newInstruments;

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

