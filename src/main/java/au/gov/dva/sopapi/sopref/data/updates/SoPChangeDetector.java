package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.interfaces.RegisterClient;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class SoPChangeDetector {

    private RegisterClient registerClient;
    private static final Logger logger = LoggerFactory.getLogger(SoPChangeDetector.class);

    public SoPChangeDetector(RegisterClient registerClient) {
        this.registerClient = registerClient;
    }

    public ImmutableSet<InstrumentChange> detectNewCompilations(ImmutableSet<String> registerIds)
    {
        List<CompletableFuture<RedirectResult>> tasks = registerIds.stream()
                .map(s -> getRedirectResult(s))
                .collect(Collectors.toList());
        try {
            List<RedirectResult> results = AsyncUtils.sequence(tasks).get();
            List<Compilation> compilations = results.stream()
                    .filter(RedirectResult::isUpdatedCompilation)
                    .map(redirectResult -> new Compilation(redirectResult.getTarget().get(), OffsetDateTime.now(),redirectResult.getSource()))
                    .collect(Collectors.toList());
            return ImmutableSet.copyOf(compilations);

        } catch (InterruptedException e) {
            logger.error("Task to get all redirect targets for all Register IDs was interrupted.",e);
            return ImmutableSet.of();
        } catch (ExecutionException e) {
            logger.error("Task to get all redirect targets for all Register IDs threw execution exception .",e);
            return ImmutableSet.of();
        }
    }


    private CompletableFuture<RedirectResult> getRedirectResult(String registerId)
    {
        return  registerClient.getRedirectTargetRegisterId(registerId)
                .handle((s, throwable) -> {
                    if (s != null)
                    {
                         return new RedirectResult(registerId,Optional.of(s));
                    }
                    else {
                        logger.error(String.format("Could not get redirect target for Register ID %s", s));
                        return new RedirectResult(registerId,Optional.empty());
                    }
                });
    }


    private class RedirectResult {
        private final String source;
        private final Optional<String> target;

        public RedirectResult(String source, Optional<String> target) {
            this.source = source;
            this.target = target;
        }

        public Optional<String> getTarget() {
            return target;
        }

        public String getSource() {
            return source;
        }

        public boolean isUpdatedCompilation()
        {
            if (!getTarget().isPresent())
                return false;

            else {
                return !target.get().contentEquals(source);
            }
        }
    }
}
