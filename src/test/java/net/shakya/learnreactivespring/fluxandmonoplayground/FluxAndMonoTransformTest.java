package net.shakya.learnreactivespring.fluxandmonoplayground;


import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class FluxAndMonoTransformTest {

  List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

  @Test
  public void transformUsingMap(){

    Flux<String> namesFlux = Flux.fromIterable(names)
        .filter(s -> s.startsWith("a"))
        .map(s -> s.toUpperCase())
        .log();

    StepVerifier.create(namesFlux)
        .expectNext("ADAM", "ANNA")
        .verifyComplete();

  }

  @Test
  public void transformUsingMap_length(){

    Flux<Integer> namesFlux = Flux.fromIterable(names)
        .map(s -> s.length())
        .log();

    StepVerifier.create(namesFlux)
        .expectNext(4,4,4,5)
        .verifyComplete();

  }

  @Test
  public void transformUsingMap_length2(){

    Flux<Integer> namesFlux = Flux.fromIterable(names)
        .map(s -> s.length())
        .repeat(1)
        .log();

    StepVerifier.create(namesFlux)
        .expectNext(4,4,4,5,4,4,4,5)
        .verifyComplete();

  }


  @Test
  public void transformUsingFlatMap(){

    Flux<String> nameFlux = Flux.fromIterable(names)
        .flatMap(s -> { return Flux.fromIterable(convertToList(s)); })
        .log();

    StepVerifier.create(nameFlux)
        .expectNextCount(8)
        .verifyComplete();
  }

  @Test
  public void transformUsingFlatMap_usingParallel(){

    Flux<String> nameFlux = Flux.fromIterable(names)
        .window(2)
        .flatMap(s ->
          s.map(this::convertToList)
              .subscribeOn(Schedulers.parallel())
        ).flatMap(s -> Flux.fromIterable(s))
        .log();

    StepVerifier.create(nameFlux)
        .expectNextCount(8)
        .verifyComplete();
  }

  @Test
  public void transformUsingFlatMap_usingParallelConcatMap(){

    Flux<String> nameFlux = Flux.fromIterable(names)
        .window(2)
        .flatMapSequential(s ->
            s.map(this::convertToList)
                .subscribeOn(Schedulers.parallel())
        ).flatMap(s -> Flux.fromIterable(s))
        .log();

    StepVerifier.create(nameFlux)
        .expectNextCount(8)
        .verifyComplete();
  }

  private List<String> convertToList(String s) {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return Arrays.asList(s, "newValue");
  }

}
