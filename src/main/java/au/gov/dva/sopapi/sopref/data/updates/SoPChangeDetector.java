package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.interfaces.RegisterClient;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SoPChangeDetector {

    private RegisterClient registerClient;
    private static final Logger logger = LoggerFactory.getLogger(SoPChangeDetector.class);

    public SoPChangeDetector(RegisterClient registerClient) {
        this.registerClient = registerClient;
    }

    public ImmutableSet<InstrumentChange> detectNewCompilations(ImmutableSet<String> registerIds)
    {
//        List<CompletableFuture<Optional<String>>> tasks = registerIds.stream()
//                .map(s -> {
//
//                       return registerClient.getRedirectTargetRegisterId(s)
//                               .handle((result, throwable) -> {
//                                   if (result != null)
//                                   {
//                                       return Optional.of(result);
//                                   }
//                                   else {
//
//                                   }
//                               })
//
//                } registerClient.getRedirectTargetRegisterId(s))
//                .

            return null;


    }

    // new instruments - nothing to do
    // check for updated compilation
    // check for repeals
}
