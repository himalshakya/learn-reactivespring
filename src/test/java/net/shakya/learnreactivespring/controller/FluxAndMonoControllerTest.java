package net.shakya.learnreactivespring.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
@ActiveProfiles("test")
public class FluxAndMonoControllerTest {


  @Autowired
  WebTestClient webTestClient;

  @Test
  public void flux_approach1(){
    Flux<Integer> integerFlux = webTestClient.get()
        .uri("/flux")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .returnResult(Integer.class)
        .getResponseBody();

    StepVerifier.create(integerFlux)
        .expectSubscription()
        .expectNext(1,2,3,4)
        .verifyComplete();
  }

  @Test
  public void flux_approach2(){
    webTestClient.get()
        .uri("/flux")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Integer.class)
        .hasSize(4);
  }

  @Test
  public void flux_approach3(){
    EntityExchangeResult<List<Integer>> expectedIntList =
        webTestClient
            .get()
            .uri("/flux")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Integer.class)
            .returnResult();

    assertEquals(expectedIntList.getResponseBody(), Arrays.asList(1,2,3,4));
  }

  @Test
  public void flux_approach4(){
      webTestClient
          .get()
          .uri("/flux")
          .accept(MediaType.APPLICATION_JSON)
          .exchange()
          .expectStatus().isOk()
          .expectBodyList(Integer.class)
          .consumeWith(response -> {
            assertEquals(Arrays.asList(1,2,3,4), response.getResponseBody());
          });

  }


  @Test
  public void fluxStream(){
    Flux<Long> longStreamFlux = webTestClient.get()
        .uri("/fluxstream")
        .accept(MediaType.APPLICATION_STREAM_JSON)
        .exchange()
        .expectStatus().isOk()
        .returnResult(Long.class)
        .getResponseBody();

    StepVerifier.create(longStreamFlux)
        .expectSubscription()
        .expectNext(0L,1L,2L,3L)
        .thenCancel()
        .verify();
  }

  @Test
  public void monoTest(){
    webTestClient
        .get()
        .uri("/mono")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Integer.class)
        .consumeWith(response -> {
          assertEquals(1, response.getResponseBody());
        });
  }
}
