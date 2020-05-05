package net.shakya.learnreactivespring.fluxandmonoplayground;


import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.StepVerifier.Step;

public class FluxAndMonoTest {

  @Test
  public void fluxTest(){
    Flux<String> stringFlux = Flux.just("spring", "spring boot", "reactive spring")
//        .concatWith(Flux.error(new RuntimeException(("Exception occured"))))
        .concatWith(Flux.just("After Error"))
        .log();
    stringFlux
        .subscribe(
            System.out::println,
            (e) -> {System.err.println(e);},
            () -> System.out.println("Completed"));
  }

  @Test
  public void fluxTestElements_WithoutError(){
    Flux<String> stringFlux1 = Flux.just("spring", "spring boot", "reactive spring")
        .log();

    StepVerifier.create(stringFlux1)
      .expectNext("spring")
        .expectNext("spring boot")
        .expectNext("reactive spring")
    .verifyComplete();
  }

  @Test
  public void fluxTestElements_WithError(){
    Flux<String> stringFlux1 = Flux.just("spring", "spring boot", "reactive spring")
        .concatWith(Flux.error(new RuntimeException(("Exception occured"))))
        .log();

    StepVerifier.create(stringFlux1)
        .expectNext("spring")
        .expectNext("spring boot")
        .expectNext("reactive spring")
        .expectError(RuntimeException.class)
    .verify();
//        .verifyComplete();
  }

  @Test
  public void fluxTestElements_WithError1(){
    Flux<String> stringFlux1 = Flux.just("spring", "spring boot", "reactive spring")
        .concatWith(Flux.error(new RuntimeException(("Exception occured"))))
        .log();

    StepVerifier.create(stringFlux1)
        .expectNext("spring", "spring boot", "reactive spring")
        .expectError(RuntimeException.class)
        .verify();
//        .verifyComplete();
  }

  @Test
  public void fluxTestElementsCount_WithError(){
    Flux<String> stringFlux1 = Flux.just("spring", "spring boot", "reactive spring")
        .concatWith(Flux.error(new RuntimeException(("Exception occured"))))
        .log();

    StepVerifier.create(stringFlux1)
        .expectNextCount(3)
        .expectError(RuntimeException.class)
        .verify();
//        .verifyComplete();
  }

  @Test
  public void monoTest(){
    Mono<String> stringMono = Mono.just("Spring");
    StepVerifier.create(stringMono)
        .expectNext("Spring")
        .verifyComplete();
  }

  @Test
  public void monoTest_Error(){
    StepVerifier.create(Mono.error(new RuntimeException("Exception")))
        .expectError(RuntimeException.class)
        .verify();
  }
}
