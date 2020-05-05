package net.shakya.learnreactivespring.fluxandmonoplayground;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoCombineTest {

  @Test
  public void combineUsingMerge(){

    Flux<String> flux1 = Flux.just("A", "B", "C");
    Flux<String> flux2 = Flux.just("1", "2", "3");

    Flux<String> mergeFlux = Flux.merge(flux1, flux2);

    StepVerifier.create(mergeFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C", "1", "2", "3")
        .verifyComplete();

  }

  @Test
  public void combineUsingMerge_withDelay(){

    Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
    Flux<String> flux2 = Flux.just("1", "2", "3").delayElements(Duration.ofSeconds(1));

    Flux<String> mergeFlux = Flux.merge(flux1, flux2);

    StepVerifier.create(mergeFlux.log())
        .expectSubscription()
        .expectNextCount(6)
//        .expectNext("A", "B", "C", "1", "2", "3")
        .verifyComplete();

  }

  @Test
  public void combineUsingMergeConcat(){

    Flux<String> flux1 = Flux.just("A", "B", "C");
    Flux<String> flux2 = Flux.just("1", "2", "3");

    Flux<String> mergeFlux = Flux.concat(flux1, flux2);

    StepVerifier.create(mergeFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C", "1", "2", "3")
        .verifyComplete();

  }

  @Test
  public void combineUsingMergeConcat_withdelay(){

    Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
    Flux<String> flux2 = Flux.just("1", "2", "3").delayElements(Duration.ofSeconds(1));

    Flux<String> mergeFlux = Flux.concat(flux1, flux2);

    StepVerifier.create(mergeFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C", "1", "2", "3")
        .verifyComplete();

  }

  @Test
  public void combineUsingMergeZip(){

    Flux<String> flux1 = Flux.just("A", "B", "C");
    Flux<String> flux2 = Flux.just("1", "2", "3");

    Flux<String> mergeFlux = Flux.zip(flux1, flux2, (t1, t2) -> t1.concat(t2));

    StepVerifier.create(mergeFlux.log())
        .expectSubscription()
        .expectNext("A1", "B2", "C3")
        .verifyComplete();

  }
}
