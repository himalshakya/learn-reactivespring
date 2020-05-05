package net.shakya.learnreactivespring.fluxandmonoplayground;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoFilterTest {

  List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

  @Test
  public void filterTest(){
    Flux<String> namesFlux = Flux.fromIterable(names)
        .filter(s -> s.startsWith("a"))
        .log();

    StepVerifier.create(namesFlux)
        .expectNext("adam", "anna")
        .verifyComplete();
  }

}