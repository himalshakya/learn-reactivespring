package net.shakya.learnreactivespring.fluxandmonoplayground;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoFactoryTest {

  List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

  @Test
  public void fluxUsingIterable(){
    Flux<String> namesFlux = Flux.fromIterable(names);

    StepVerifier.create(namesFlux)
        .expectNext("adam", "anna", "jack", "jenny")
        .verifyComplete();
  }

  @Test
  public void fluxUsingArray(){
    String[] namesArray = names.toArray(new String[0]);
    Flux<String> namesFlux = Flux.fromArray(namesArray);
    StepVerifier.create(namesFlux)
        .expectNext("adam", "anna", "jack", "jenny")
        .verifyComplete();

  }

  @Test
  public void fluxUsingStream(){
    Flux<String> namesFlux = Flux.fromStream(names.stream());
    StepVerifier.create(namesFlux)
        .expectNext("adam", "anna", "jack", "jenny")
        .verifyComplete();
  }

  @Test
  public void monoUsingJustOrEmpty(){
    Mono<String> mono = Mono.justOrEmpty(null);
    StepVerifier.create(mono)
        .verifyComplete();
  }

  @Test
  public void monoUsingSupplier(){
    Mono<String> mono = Mono.fromSupplier(() -> "adam");
    StepVerifier.create(mono.log())
        .expectNext("adam")
        .verifyComplete();
  }

  @Test
  public void fluxUsingRange(){
    Flux<Integer> intFlux =  Flux.range(1, 5);
    StepVerifier.create(intFlux)
        .expectNext(1,2,3,4,5)
        .verifyComplete();

  }
}
