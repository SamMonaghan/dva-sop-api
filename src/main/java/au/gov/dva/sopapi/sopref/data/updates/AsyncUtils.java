package au.gov.dva.sopapi.sopref.data.updates;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

class AsyncUtils {


 // http://www.nurkiewicz.com/2013/05/java-8-completablefuture-in-action.html
 public static <T> CompletableFuture<List<Optional<T>>> sequence(List<CompletableFuture<Optional<T>>> futures) {
  CompletableFuture<Void> allDoneFuture =
          CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
  return allDoneFuture.thenApply(v ->
          futures.stream().
                  map(future -> future.join()).
                  collect(Collectors.toList())
  );
 }

}
