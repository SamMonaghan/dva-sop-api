package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.AppSettings;
import au.gov.dva.sopapi.interfaces.model.LegislationRegisterEmailUpdate;
import com.google.common.collect.ImmutableSet;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LegislationRegisterEmailUpdates {

    public static CompletableFuture<ImmutableSet<LegislationRegisterEmailUpdate>> getLatestAfter(OffsetDateTime afterDate, String senderAddress) {

        CompletableFuture<ImmutableSet<LegislationRegisterEmailUpdate>> future = CompletableFuture.supplyAsync(new Supplier<ImmutableSet<LegislationRegisterEmailUpdate>>() {
            @Override
            public ImmutableSet<LegislationRegisterEmailUpdate> get() {
                return ImmutableSet.copyOf(getUpdatesFromLatestAfter(afterDate, senderAddress));
            }
        });

        return future;
    }

    private static Set<LegislationRegisterEmailUpdate> getUpdatesFromLatestAfter(OffsetDateTime afterDate, String senderAddress) {
        String emailAddress = AppSettings.LegislationRegisterEmailSubscription.getUserId();
        String emailPassword = AppSettings.LegislationRegisterEmailSubscription.getPassword();

        Properties props = new Properties();

        try {
            InputStream propertiesStream = LegislationRegisterEmailUpdates.class.getClassLoader().getResourceAsStream("smtp.properties");
            props.load(propertiesStream);
            Session emailSession = Session.getDefaultInstance(props, null);
            Store store = emailSession.getStore("imaps");
            store.connect("smtp.gmail.com", emailAddress, emailPassword);

            Folder inbox = store.getFolder("inbox");
            inbox.open(Folder.READ_ONLY);

            Message latestMessage = null;
            OffsetDateTime latestSentDate = null;

            Message[] messages = inbox.getMessages();
            for (Message msg : messages) {
                if (msg instanceof MimeMessage) {
                    Address sender = ((MimeMessage) msg).getSender();
                    if (sender.toString().contains(senderAddress)) {
                        OffsetDateTime sentDate = OffsetDateTime.ofInstant(msg.getSentDate().toInstant(), ZoneId.systemDefault());
                        if (latestMessage == null) {
                            if (sentDate.isAfter(afterDate)) {
                                latestMessage = msg;
                                latestSentDate = sentDate;
                            }
                        } else {
                            if (sentDate.isAfter(afterDate) && sentDate.isAfter(latestSentDate)) {
                                latestMessage = msg;
                                latestSentDate = sentDate;
                            }
                        }

                    }
                }
            }

            if (latestMessage != null) {
                Set<LegislationRegisterEmailUpdate> updates = parseMessage(latestMessage, latestSentDate);
                return updates;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }

        return Collections.EMPTY_SET;
    }

    private static Set<LegislationRegisterEmailUpdate> parseMessage(Message msg, OffsetDateTime sentDate) throws IOException, MessagingException {
        Object content = msg.getContent();
        if (content instanceof MimeMultipart) {
            MimeMultipart mimeMultipartContent = (MimeMultipart) content;
            for (int i = 0; i < mimeMultipartContent.getCount(); i++) {
                BodyPart part = mimeMultipartContent.getBodyPart(i);
                if (part.getContentType().contains("TEXT/HTML")) {
                    String msgContent = (String) part.getContent();

                    int legislativeInstrumentsStart = msgContent.indexOf("Legislative Instruments");
                    int legislativeInstrumentCompilationsStart = msgContent.indexOf("Legislative Instrument Compilations");
                    int subscriptionDetailsStart = msgContent.indexOf("SUBSCRIPTION DETAILS");

                    String legislativeInstrumentsSection = null;
                    String legislativeInstrumentCompilationsSection = null;

                    if (legislativeInstrumentsStart != -1) {
                        if (legislativeInstrumentCompilationsStart != -1) {
                            legislativeInstrumentsSection = msgContent.substring(legislativeInstrumentsStart, legislativeInstrumentCompilationsStart);
                        } else {
                            legislativeInstrumentsSection = msgContent.substring(legislativeInstrumentsStart, subscriptionDetailsStart);
                        }
                    }

                    if (legislativeInstrumentCompilationsStart != -1) {
                        legislativeInstrumentCompilationsSection = msgContent.substring(legislativeInstrumentCompilationsStart, subscriptionDetailsStart);
                    }

                    List<LegislationRegisterEmailUpdate> instrumentUpdates = new ArrayList<>();
                    List<LegislationRegisterEmailUpdate> compilationUpdates = new ArrayList<>();

                    if (legislativeInstrumentsSection != null) {
                        instrumentUpdates = processLegislativeInstruments(legislativeInstrumentsSection, sentDate);
                    }

                    if (legislativeInstrumentCompilationsSection != null) {
                        compilationUpdates = processLegislativeInstrumentCompilations(legislativeInstrumentCompilationsSection, sentDate);
                    }

                    Set<LegislationRegisterEmailUpdate> updatesSet = new HashSet<>();
                    updatesSet.addAll(instrumentUpdates);
                    updatesSet.addAll(compilationUpdates);

                    return updatesSet;
                }
            }
        }

        return null;
    }

    private static List<LegislationRegisterEmailUpdate> processLegislativeInstruments(String legislativeInstrumentsSection, OffsetDateTime sentDate) {
        String[] lines = legislativeInstrumentsSection.split("<br>");
        // discard the header lines
        lines = Arrays.copyOfRange(lines, 2, lines.length);

        List<LegislationRegisterEmailUpdate> updates = new ArrayList<>();
        int currentUpdateLineNumber = -1;

        Pattern urlMatchPattern = Pattern.compile("a href=\"([^\"]+)\"");

        LegislationRegisterEmailUpdateImpl currentUpdate = null;
        for (String line: lines) {
            if (line.equals("")) {
                if (currentUpdate != null) {
                    currentUpdate = null;
                    currentUpdateLineNumber = -1;
                }
            } else {
                if (currentUpdateLineNumber == -1) {
                    currentUpdateLineNumber = 1;
                    currentUpdate = new LegislationRegisterEmailUpdateImpl();
                    updates.add(currentUpdate);
                    currentUpdate.setDateReceived(sentDate);
                } else {
                    currentUpdateLineNumber++;
                }

                if (currentUpdateLineNumber == 1) {
                    currentUpdate.setInstrumentTitle(line);
                } else if (currentUpdateLineNumber == 2) {
                    currentUpdate.setInstrumentDescription(line);
                } else if (currentUpdateLineNumber == 3) {
                    currentUpdate.setUpdateDescription(line);
                } else if (currentUpdateLineNumber == 4) {
                    Matcher urlMatcher = urlMatchPattern.matcher(line);
                    if (urlMatcher.find()) {
                        String strUrl = urlMatcher.group(1);

                        try {
                            currentUpdate.setRegisterLink(new URL(strUrl));
                        } catch (MalformedURLException ex ) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    throw new IllegalStateException("Invalid number of lines for instrument update");
                }
            }
        }

        return updates;
    }

    private static List<LegislationRegisterEmailUpdate> processLegislativeInstrumentCompilations(String legislativeInstrumentCompilationsSection, OffsetDateTime sentDate) {
        String[] lines = legislativeInstrumentCompilationsSection.split("<br>");
        // discard the header lines
        lines = Arrays.copyOfRange(lines, 2, lines.length);

        List<LegislationRegisterEmailUpdate> updates = new ArrayList<>();
        int currentUpdateLineNumber = -1;

        Pattern urlMatchPattern = Pattern.compile("a href=\"([^\"]+)\"");

        LegislationRegisterEmailUpdateImpl currentUpdate = null;
        for (String line: lines) {
            if (line.equals("")) {
                if (currentUpdate != null) {
                    currentUpdate = null;
                    currentUpdateLineNumber = -1;
                }
            } else {
                if (currentUpdateLineNumber == -1) {
                    currentUpdateLineNumber = 1;
                    currentUpdate = new LegislationRegisterEmailUpdateImpl();
                    updates.add(currentUpdate);
                    currentUpdate.setDateReceived(sentDate);
                } else {
                    currentUpdateLineNumber++;
                }

                if (currentUpdateLineNumber == 1) {
                    currentUpdate.setInstrumentTitle(line);
                } else if (currentUpdateLineNumber == 2) {
                    currentUpdate.setUpdateDescription(line);
                } else if (currentUpdateLineNumber == 3) {
                    Matcher urlMatcher = urlMatchPattern.matcher(line);
                    if (urlMatcher.find()) {
                        String strUrl = urlMatcher.group(1);

                        try {
                            currentUpdate.setRegisterLink(new URL(strUrl));
                        } catch (MalformedURLException ex ) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    throw new IllegalStateException("Invalid number of lines for instrument compilation update");
                }
            }
        }

        return updates;
    }

    public static class LegislationRegisterEmailUpdateImpl implements LegislationRegisterEmailUpdate {

        private String instrumentTitle;
        private String instrumentDescription;
        private String updateDescription;
        private URL registerLink;
        private OffsetDateTime dateReceived;

        @Override
        public String getInstrumentTitle() {
            return instrumentTitle;
        }

        @Override
        public Optional<String> getInstrumentDescription() {
            return Optional.ofNullable(instrumentDescription);
        }

        @Override
        public String getUpdateDescription() {
            return updateDescription;
        }

        @Override
        public URL getRegisterLink() {
            return registerLink;
        }

        @Override
        public OffsetDateTime getDateReceived() {
            return dateReceived;
        }

        public void setInstrumentTitle(String instrumentTitle) {
            this.instrumentTitle = instrumentTitle;
        }

        public void setInstrumentDescription(String instrumentDescription) {
            this.instrumentDescription = instrumentDescription;
        }

        public void setUpdateDescription(String updateDescription) {
            this.updateDescription = updateDescription;
        }

        public void setRegisterLink(URL registerLink) {
            this.registerLink = registerLink;
        }

        public void setDateReceived(OffsetDateTime dateReceived) {
            this.dateReceived = dateReceived;
        }
    }
}
