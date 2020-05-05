package net.shakya.learnreactivespring.repository;

import java.util.Arrays;
import java.util.List;
import net.shakya.learnreactivespring.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@DataMongoTest
@RunWith(SpringRunner.class)
@DirtiesContext
@ActiveProfiles("test")
public class ItemReactiveRepositoryTest {

  @Autowired
  ItemReactiveRepository itemReactiveRepository;

  List<Item> items = Arrays.asList(
      new Item(null, "Samsung TV", 400.0),
      new Item(null, "LG TV", 420.0),
      new Item(null, "Apple Watch", 299.99),
      new Item(null, "Beats Headphone", 149.99),
      new Item("ABC", "Bose Headphone", 149.99)
  );

  @BeforeEach
  public void setUp(){
    itemReactiveRepository.deleteAll()
        .thenMany(Flux.fromIterable(items))
        .flatMap(itemReactiveRepository::save)
        .doOnNext( item -> {
          System.out.println("Inserted Item is " + item);
        })
    .blockLast();
  }

  @Test
  public void getAllItems(){
    StepVerifier
        .create(itemReactiveRepository.findAll())
        .expectSubscription()
        .expectNextCount(5)
        .verifyComplete();
  }

  @Test
  public void getItemByID(){
    StepVerifier
        .create(itemReactiveRepository.findById("ABC"))
        .expectSubscription()
        .expectNextMatches(item -> item.getDescription().equals("Bose Headphone"))
        .verifyComplete();
  }

  @Test
  public void findByDescriptionTest(){
    StepVerifier
        .create(itemReactiveRepository.findByDescription("Bose Headphone"))
        .expectSubscription()
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  public void saveItem(){
    Item item = new Item(null, "Google Home Mini", 30.00);
    Mono<Item> savedItem = itemReactiveRepository.save(item);
    StepVerifier.create(savedItem.log())
        .expectSubscription()
        .expectNextMatches(item1 -> item1.getId() != null && item1.getDescription().equals("Google Home Mini"))
        .verifyComplete();
  }

  @Test
  public void updateItem(){

    final double newPrice = 520.00;
    Flux<Item> updatedItem = itemReactiveRepository.findByDescription("LG TV")
        .map(item -> {
          item.setPrice(newPrice);
          return item;
        })
        .flatMap(item -> {
          return itemReactiveRepository.save(item);
        });


    StepVerifier.create(updatedItem.log())
        .expectSubscription()
        .expectNextMatches(item -> item.getPrice() == newPrice)
        .verifyComplete();
  }

  @Test
  public void deleteItemById(){
    Mono<Void> deletedItem = itemReactiveRepository.findById("ABC")
        .map(Item::getId)
        .flatMap(id -> {
          return itemReactiveRepository.deleteById(id);
        });

    StepVerifier.create(deletedItem.log())
        .expectSubscription()
        .verifyComplete();

    StepVerifier
        .create(itemReactiveRepository.findById("ABC"))
        .expectSubscription()
        .expectNextCount(0)
        .verifyComplete();

    StepVerifier
        .create(itemReactiveRepository.findAll().log("Remaining Item: "))
        .expectSubscription()
        .expectNextCount(4)
        .verifyComplete();
  }

  @Test
  public void deleteItem(){
    Flux<Void> deletedItem = itemReactiveRepository.findByDescription("LG TV")
        .flatMap(item -> {
          return itemReactiveRepository.delete(item);
        });

    StepVerifier.create(deletedItem.log())
        .expectSubscription()
        .verifyComplete();

    StepVerifier
        .create(itemReactiveRepository.findByDescription("LG TV"))
        .expectSubscription()
        .expectNextCount(0)
        .verifyComplete();

    StepVerifier
        .create(itemReactiveRepository.findAll().log("Remaining Item: "))
        .expectSubscription()
        .expectNextCount(4)
        .verifyComplete();
  }
}
