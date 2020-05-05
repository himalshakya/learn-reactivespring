package net.shakya.learnreactivespring.fluxandmonoplayground;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoErrorTest {

  @Test
  public void fluxErrorHandling(){
    Flux<String> stringFlux = Flux.just("A", "B", "C")
        .concatWith(Flux.error(new RuntimeException(("Exception occured"))))
        .concatWith(Flux.just("D"))
        .onErrorResume(e -> { System.out.println("Error : " + e);
        return Flux.just("default", "default1"); });

    StepVerifier.create(stringFlux)
        .expectNext("A","B","C")
        .expectNext("default", "default1")
        .verifyComplete();
  }

  @Test
  public void fluxErrorHandling_OnErrorReturn(){
    Flux<String> stringFlux = Flux.just("A", "B", "C")
        .concatWith(Flux.error(new RuntimeException(("Exception occured"))))
        .concatWith(Flux.just("D"))
        .onErrorReturn("default");

    StepVerifier.create(stringFlux)
        .expectNext("A","B","C")
        .expectNext("default")
        .verifyComplete();
  }

  @Test
  public void fluxErrorHandling_OnErrorMap(){
    Flux<String> stringFlux = Flux.just("A", "B", "C")
        .concatWith(Flux.error(new RuntimeException(("Exception occured"))))
        .concatWith(Flux.just("D"))
        .onErrorMap(e -> new CustomException(e));

    StepVerifier.create(stringFlux.log())
        .expectNext("A","B","C")
        .expectError(CustomException.class)
        .verify();
  }


  @Test
  public void fluxErrorHandling_OnErrorMap_with_retry(){
    Flux<String> stringFlux = Flux.just("A", "B", "C")
        .concatWith(Flux.error(new RuntimeException(("Exception occured"))))
        .concatWith(Flux.just("D"))
        .onErrorMap(e -> new CustomException(e))
        .retry(2);

    StepVerifier.create(stringFlux.log())
        .expectNext("A","B","C")
        .expectNext("A","B","C")
        .expectNext("A","B","C")
        .expectError(CustomException.class)
        .verify();
  }

  @Test
  public void fluxErrorHandling_OnErrorMap_with_retryBackoff(){
    Flux<String> stringFlux = Flux.just("A", "B", "C")
        .concatWith(Flux.error(new RuntimeException(("Exception occured"))))
        .concatWith(Flux.just("D"))
        .onErrorMap(e -> new CustomException(e))
        .retryBackoff(2, Duration.ofSeconds(5));

    StepVerifier.create(stringFlux.log())
        .expectNext("A","B","C")
        .expectNext("A","B","C")
        .expectNext("A","B","C")
        .expectError(IllegalStateException.class)
        .verify();
  }

}
