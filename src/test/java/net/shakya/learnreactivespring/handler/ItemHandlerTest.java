package net.shakya.learnreactivespring.handler;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import net.shakya.learnreactivespring.constants.ItemConstants;
import net.shakya.learnreactivespring.document.Item;
import net.shakya.learnreactivespring.repository.ItemReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
@ActiveProfiles("test")
public class ItemHandlerTest {

  @Autowired
  WebTestClient webTestClient;

  @Autowired
  ItemReactiveRepository itemRepository;

  private List<Item> data() {
    return Arrays.asList(
        new Item(null, "Samsung TV", 400.0),
        new Item(null, "LG TV", 329.99),
        new Item(null, "Apple Watch", 349.99),
        new Item("ABC", "Beats Headphone", 19.99)
    );
  }

  @BeforeEach
  public void setUp(){
    itemRepository
        .deleteAll()
        .thenMany(Flux.fromIterable(data()))
        .flatMap(itemRepository::save)
        .doOnNext(
            item -> {
              System.out.println("Inserted Item is : " + item);
            }
        )
        .blockLast();
  }

  @Test
  public void getAllItems(){
    webTestClient
        .get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Item.class)
        .hasSize(4);
  }

  @Test
  public void getAllItems_approach_2(){
    webTestClient
        .get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Item.class)
        .hasSize(4)
        .consumeWith(
            response -> {
              List<Item> items = response.getResponseBody();
              items.forEach(
                  item -> assertTrue(item.getId() != null)
              );
            }
        );
  }

  @Test
  public void getAllItems_approach_3(){
    Flux<Item> itemFlux = webTestClient
        .get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .returnResult(Item.class)
        .getResponseBody();

    StepVerifier
        .create(itemFlux)
        .expectSubscription()
        .expectNextCount(4)
        .verifyComplete();
  }

  @Test
  public void getOneItemTest(){
    webTestClient
        .get()
        .uri((ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}")), "ABC")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.price", 19.99);

  }

  @Test
  public void getOneItem_NotFound_Test(){
    webTestClient
        .get()
        .uri((ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}")), "DEF")
        .exchange()
        .expectStatus().isNotFound();
  }


  @Test
  public void createItem(){
    Item item = new Item(null, "iPhone X", 999.99);

    webTestClient
        .post()
        .uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(item), Item.class)
        .exchange()
        .expectStatus().isCreated()
        .expectBody()
        .jsonPath("$.id").isNotEmpty()
        .jsonPath("$.description").isEqualTo("iPhone X")
        .jsonPath("$.price").isEqualTo(999.99);

  }

  @Test
  public void deleteItemTest(){
    webTestClient
        .delete()
        .uri((ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}")), "ABC")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Void.class);

  }

  @Test
  public void updateItemTest(){
    String id = "ABC";
    double newPrice = 129.99;
    Item item = new Item(id, "Beats HeadPhones", newPrice);

    webTestClient
        .put()
        .uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), id)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(item), Item.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.id").isEqualTo(id)
        .jsonPath("$.description").isEqualTo("Beats HeadPhones")
        .jsonPath("$.price").isEqualTo(newPrice);
  }

  @Test
  public void updateItem_NotFound_Test(){
    String id = "DEF";
    double newPrice = 129.99;
    Item item = new Item(id, "Beats HeadPhones", newPrice);

    webTestClient
        .put()
        .uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), id)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(item), Item.class)
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  public void runtimeExceptionTest(){
    webTestClient
        .get()
        .uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 + "_runtimeExp")
        .exchange()
        .expectStatus().is5xxServerError()
        .expectBody()
        .jsonPath("$.message", "RuntimeException occurred");

  }
}
