package au.gov.dva.sopapi.interfaces;

import java.util.concurrent.CompletableFuture;

public interface RegisterClient {
    CompletableFuture<byte[]> getAuthorisedInstrumentPdf(String registerId);
}
