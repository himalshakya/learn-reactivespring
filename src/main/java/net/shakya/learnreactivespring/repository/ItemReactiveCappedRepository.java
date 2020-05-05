package net.shakya.learnreactivespring.repository;

import net.shakya.learnreactivespring.document.ItemCapped;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface ItemReactiveCappedRepository extends ReactiveMongoRepository<ItemCapped, String> {

  @Tailable
  public Flux<ItemCapped> findItemsBy();
}
