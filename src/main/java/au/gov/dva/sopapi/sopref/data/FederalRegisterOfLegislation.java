package au.gov.dva.sopapi.sopref.data;

import au.gov.dva.sopapi.exceptions.LegislationRegisterError;
import au.gov.dva.sopapi.interfaces.RegisterClient;
import org.asynchttpclient.AsyncHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class FederalRegisterOfLegislation implements RegisterClient {

    private static final String BASE_URL = "https://www.legislation.gov.au";

    public class AuthorisedInstrumentResult {

        private final String registerId;
        private final byte[] pdfBytes;

        public AuthorisedInstrumentResult(String registerId, byte[] pdfBytes) {
            this.registerId = registerId;
            this.pdfBytes = pdfBytes;
        }

        public byte[] getPdfBytes() {
            return pdfBytes;
        }

        public String getRegisterId() {
            return registerId;
        }

    }

    final static Logger logger = LoggerFactory.getLogger(FederalRegisterOfLegislation.class);

    public CompletableFuture<AuthorisedInstrumentResult> getAuthorisedInstrument(String registerId) {
        URL latestDownloadPageUrl;
        try {
            latestDownloadPageUrl = new URL(buildUrlForLatestDownloadPage(registerId));
        } catch (MalformedURLException e) {
            throw new LegislationRegisterError(e);
        }

        return null;
    }

    public CompletableFuture<String> getRedirectTargetRegisterId(String registerId) {
        URL urlToGetRedirect;
        try {
            urlToGetRedirect = new URL(buildUrlToGetRedirect(registerId));
        } catch (MalformedURLException e) {
            throw new LegislationRegisterError(e);
        }
        return getRedirectTargetUrl(urlToGetRedirect)
                .thenApply(url -> extractTargetRegisterIdFromRedirectUrl(url))


    }

    private static String extractTargetRegisterIdFromRedirectUrl(URL redirectTargetUrl)
    {
        String[] pathParts = redirectTargetUrl.getPath().split("//");
        return pathParts[pathParts.length - 1];
    }

    @Override
    public CompletableFuture<byte[]> getAuthorisedInstrumentPdf(String registerId) {
        URL latestDownloadPageUrl;
        try {
            latestDownloadPageUrl = new URL(buildUrlForLatestDownloadPage(registerId));
        } catch (MalformedURLException e) {
            throw new LegislationRegisterError(e);
        }

        CompletableFuture<byte[]> promise = getRedirectTargetUrl(latestDownloadPageUrl)
                .thenCompose(url -> downloadHtml(url))
                .thenApply(htmlString -> {
                    try {
                        return getAuthorisedDocumentLinkFromHtml(htmlString, registerId);
                    } catch (MalformedURLException e) {
                        throw new LegislationRegisterError(e);
                    }
                })
                .thenCompose(url -> downloadFile(url));

        return promise;
    }


    public static CompletableFuture<byte[]> downloadFile(URL url) {
        AsyncHttpClient asyncHttpClient = asyncHttpClient();
        CompletableFuture<byte[]> promise = asyncHttpClient
                .prepareGet(url.toString())
                .execute()
                .toCompletableFuture()
                .thenApply(response -> response.getResponseBodyAsBytes());
        return promise;
    }

    public static CompletableFuture<String> downloadHtml(URL url) {
        AsyncHttpClient asyncHttpClient = asyncHttpClient();
        CompletableFuture<String> promise = asyncHttpClient
                .prepareGet(url.toString())
                .execute()
                .toCompletableFuture()
                .thenApply(response -> response.getResponseBody());
        return promise;
    }

    public static CompletableFuture<URL> getRedirectTargetUrl(URL originalUrl) {
        assert (originalUrl.getHost().startsWith("www"));
        AsyncHttpClient asyncHttpClient = asyncHttpClient();
        CompletableFuture<URL> promise = asyncHttpClient
                .prepareGet(originalUrl.toString())
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    assert (response.getStatusCode() == 302);
                    String redirectValue = response.getHeader("Location");
                    if (redirectValue.isEmpty())
                        throw new LegislationRegisterError(String.format("Could not get redirect to Details page from URL: %s\n%s", originalUrl.toString(), response.toString()));
                    try {
                        assert (!URI.create(redirectValue).isAbsolute() && redirectValue.startsWith("/"));
                        URL redirectTarget = URI.create(String.format("%s://%s%s", originalUrl.getProtocol(), originalUrl.getHost(), redirectValue)).toURL();
                        return redirectTarget;
                    } catch (MalformedURLException e) {
                        throw new LegislationRegisterError(e);
                    }
                });

        return promise;
    }

    public static URL getAuthorisedDocumentLinkFromHtml(String html, String registerID) throws MalformedURLException {
        Document htmlDocument = Jsoup.parse(html);
        // Note that currently at legislation.gov.au there is an additional space in the title for this element - probably an error.
        // There is additional selector with one space so this will still work if the devs fix that error.
        String cssSelector = String.format("a[title*=\"%s  authorised version\"], [a[title*=\"%s authorised version\"]", registerID, registerID);
        Elements elements = htmlDocument.select(cssSelector);
        assert !elements.isEmpty();
        String linkUrl = elements.attr("href");
        assert !linkUrl.isEmpty();
        return URI.create(linkUrl).toURL();
    }


    public static Optional<String> getTitleStatus(String html) {

//        <li id="MainContent_ucLegItemPane_liStatus" class="info2">
//            <span id="MainContent_ucLegItemPane_lblTitleStatus" class="RedText">No longer in force</span>
//
//            <span id="MainContent_ucLegItemPane_lblVersionStatus" class="RedText"></span>
//        </li>

        return getCssIdValue(html, "MainContent_ucLegItemPane_lblTitleStatus");
    }


    public static Optional<String> getVersionStatus(String html) {

        return getCssIdValue(html, "MainContent_ucLegItemPane_lblVersionStatus");
    }


    public static Optional<String> getRegisterIdOfRepealedByCeasedBy(String html) {
        Document htmlDocument = Jsoup.parse(html);
        String cssSelector = String.format("a[id*='SeriesRepealedBy']");
        Elements elements = htmlDocument.select(cssSelector);
        assert !elements.isEmpty();
        String linkUrl = elements.attr("href");

        assert !linkUrl.isEmpty();
        Pattern pattern = Pattern.compile("(F[0-9]{4,4}[A-Z][0-9]+)");
        Matcher matcher = pattern.matcher(linkUrl);

        if (!matcher.find())
            return Optional.empty();

        String registerId = matcher.group(1);
        return Optional.of(registerId);
    }

    public static String getLatestCompilation(String registerId) {
        // eg https://www.legislation.gov.au/Series/F2014L01389/Compilations


        return null;
    }


    private static Optional<String> getCssIdValue(String html, String id) {
        Document htmlDocument = Jsoup.parse(html);
        String cssSelector = String.format("#%s", id);
        Elements elements = htmlDocument.select(cssSelector);
        if (elements.isEmpty()) {
            logger.error(String.format("Could not determine current status of instrument using selector '%s' from HTML: \n%s", cssSelector, html));
            return Optional.empty();
        }
        Element element = elements.first();
        String status = element.text();
        if (status.isEmpty()) {
            logger.error(String.format("Empty string value for status using selector '%s' from HTML: \n%s", cssSelector, html));
            return Optional.empty();
        }
        return Optional.of(status.trim());
    }

    private static String buildUrlForLatestDownloadPage(String registerId) {
        return String.format("%s/Latest/%s/Download", BASE_URL, registerId);
    }

    private static String buildUrlToGetRedirect(String sourceRegisterId) {
        return String.format("%s/Latest/%s", BASE_URL, sourceRegisterId);
    }

}
