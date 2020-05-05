package net.shakya.learnreactivespring.fluxandmonoplayground;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoWithTest {

  @Test
  public void infiniteSequence(){
    Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(100)).log();

    infiniteFlux.subscribe(element -> System.out.println(element));

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void infiniteSequenceTest(){
    Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(100)).take(3).log();
    StepVerifier.create(finiteFlux)
        .expectSubscription()
        .expectNext(0L, 1L, 2L)
        .verifyComplete();
  }

  @Test
  public void infiniteSequenceMapTest(){
    Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
        .delayElements(Duration.ofSeconds(1))
        .map(l -> new Integer(l.intValue())).take(3).log();

    StepVerifier.create(finiteFlux)
        .expectSubscription()
        .expectNext(0, 1, 2)
        .verifyComplete();
  }
}
