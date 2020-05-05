package net.shakya.learnreactivespring.controller.v1;

import net.shakya.learnreactivespring.constants.ItemConstants;
import net.shakya.learnreactivespring.document.ItemCapped;
import net.shakya.learnreactivespring.repository.ItemReactiveCappedRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ItemStreamController {

  ItemReactiveCappedRepository repository;

  public ItemStreamController(
      ItemReactiveCappedRepository repository) {
    this.repository = repository;
  }

  @GetMapping(value = ItemConstants.ITEM_STREAM_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
  public Flux<ItemCapped> getItemsStream(){
    return repository.findItemsBy();
  }
}
