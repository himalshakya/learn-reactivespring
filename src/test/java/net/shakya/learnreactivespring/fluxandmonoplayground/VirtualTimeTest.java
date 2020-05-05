package net.shakya.learnreactivespring.fluxandmonoplayground;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

public class VirtualTimeTest {

  @Test
  public void testingWithoutVirtualTime(){

    Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1)).take(3);

    StepVerifier.create(longFlux.log())
        .expectSubscription()
        .expectNext(0L, 1L, 2L)
        .verifyComplete();

  }

  @Test
  public void testingWithVirtualTime(){

    VirtualTimeScheduler.getOrSet();

    Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1)).take(3);

    StepVerifier
        .withVirtualTime(() -> longFlux.log())
        .expectSubscription()
        .thenAwait(Duration.ofSeconds(3))
        .expectNext(0L, 1L, 2L)
        .verifyComplete();

  }

  @Test
  public void combineUsingMergeConcat_withdelay(){

    VirtualTimeScheduler.getOrSet();

    Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
    Flux<String> flux2 = Flux.just("1", "2", "3").delayElements(Duration.ofSeconds(1));

    Flux<String> mergeFlux = Flux.concat(flux1, flux2);

    StepVerifier
        .withVirtualTime(() -> mergeFlux.log())
        .expectSubscription()
        .thenAwait(Duration.ofSeconds(6))
        .expectNextCount(6)
        .verifyComplete();

  }
}
