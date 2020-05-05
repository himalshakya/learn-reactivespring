package net.shakya.learnreactivespring.initialize;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import net.shakya.learnreactivespring.document.Item;
import net.shakya.learnreactivespring.document.ItemCapped;
import net.shakya.learnreactivespring.repository.ItemReactiveCappedRepository;
import net.shakya.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

  ItemReactiveRepository repository;
  ItemReactiveCappedRepository itemReactiveCappedRepository;
  MongoOperations mongoOperations;

  public ItemDataInitializer(
      ItemReactiveRepository repository,
      ItemReactiveCappedRepository itemReactiveCappedRepository,
      MongoOperations mongoOperations) {
    this.repository = repository;
    this.itemReactiveCappedRepository = itemReactiveCappedRepository;
    this.mongoOperations = mongoOperations;
  }

  @Override
  public void run(String... args) throws Exception {
    initialDataSetup();
    createCappedCollections();
    dataSetUpForCappedCollection();
  }

  private void createCappedCollections() {
    this.mongoOperations.dropCollection(ItemCapped.class);
    this.mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());
  }

  private void initialDataSetup() {
    repository
        .deleteAll()
        .thenMany(Flux.fromIterable(data()))
        .flatMap(repository::save)
        .thenMany(repository.findAll())
        .subscribe(item -> {
          System.out.println("Item inserted from Command Line Runner : " + item);
        });
  }

  public void dataSetUpForCappedCollection(){
    Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
        .map(i -> new ItemCapped(null, "Random Item " + i, ThreadLocalRandom
            .current().nextDouble(100, 2000)));

    itemReactiveCappedRepository
        .insert(itemCappedFlux)
    .subscribe(itemCapped -> {
      log.info("itemCapped Added : " + itemCapped);
    });
  }

  private List<Item> data() {
    return Arrays.asList(
        new Item(null, "Samsung TV", 400.0),
        new Item(null, "LG TV", 329.99),
        new Item(null, "Apple Watch", 349.99),
        new Item("ABC", "Beats Headphone", 19.99)
    );
  }
}
