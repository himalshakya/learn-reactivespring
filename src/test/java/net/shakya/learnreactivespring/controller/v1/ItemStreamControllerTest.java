package net.shakya.learnreactivespring.controller.v1;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.shakya.learnreactivespring.constants.ItemConstants;
import net.shakya.learnreactivespring.document.Item;
import net.shakya.learnreactivespring.document.ItemCapped;
import net.shakya.learnreactivespring.repository.ItemReactiveCappedRepository;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemStreamControllerTest {

  @Autowired
  WebTestClient webTestClient;

  @Autowired
  MongoOperations operations;

  @Autowired
  ItemReactiveCappedRepository repository;

  @BeforeEach
  public void setUp(){
    operations.dropCollection(ItemCapped.class);
    operations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(500).capped());

    Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofMillis(1))
        .map(i -> new ItemCapped(null, "Random Item " + i, ThreadLocalRandom
            .current().nextDouble(100, 2000)))
        .take(10);

    repository
        .insert(itemCappedFlux)
        .doOnNext(itemCapped -> {
          System.out.println("Inserted Item in setUp " + itemCapped);
        })
        .blockLast();
  }

  @Test
  public void getStreamAllItems(){
    Flux<ItemCapped> itemCappedFlux = webTestClient
        .get()
        .uri(ItemConstants.ITEM_STREAM_END_POINT_V1)
        .exchange()
        .expectStatus().isOk()
        .returnResult(ItemCapped.class)
        .getResponseBody()
        .take(5);

    StepVerifier.create(itemCappedFlux.log("--> "))
        .expectNextCount(3)
        .thenCancel()
        .verify();
  }
}
