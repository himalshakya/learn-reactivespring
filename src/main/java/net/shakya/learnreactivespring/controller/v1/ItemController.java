package net.shakya.learnreactivespring.controller.v1;

import lombok.extern.slf4j.Slf4j;
import net.shakya.learnreactivespring.constants.ItemConstants;
import net.shakya.learnreactivespring.document.Item;
import net.shakya.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemController {

  ItemReactiveRepository repository;

  public ItemController(ItemReactiveRepository repository) {
    this.repository = repository;
  }

  @GetMapping(ItemConstants.ITEM_END_POINT_V1)
  public Flux<Item> getAllItems(){
    return this.repository.findAll();
  }

  @GetMapping(ItemConstants.ITEM_END_POINT_V1 + "/{id}")
  public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id){
    return this.repository.findById(id)
        .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PostMapping(ItemConstants.ITEM_END_POINT_V1)
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Item> createItem(@RequestBody Item item){
    return repository.save(item);
  }

  @DeleteMapping(ItemConstants.ITEM_END_POINT_V1 + "/{id}")
  public Mono<Void> deleteItem(@PathVariable String id){
    return repository.deleteById(id);
  }

  @PutMapping(ItemConstants.ITEM_END_POINT_V1 + "/{id}")
  public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id, @RequestBody Item item){
    return this.repository.findById(id)
        .flatMap(currentItem -> {
          currentItem.setPrice(item.getPrice());
          currentItem.setDescription(item.getDescription());
          return repository.save(currentItem);
        })
        .map(updatedItem -> new ResponseEntity<>(updatedItem, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping(ItemConstants.ITEM_END_POINT_V1 + "/runtimeExp")
  public Flux<Item> runtimeException(){
    return repository.findAll()
        .concatWith(Mono.error(new RuntimeException(("Runtime Exception Occurred"))));
  }
}
