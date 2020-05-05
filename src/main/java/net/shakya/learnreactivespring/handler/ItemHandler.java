package net.shakya.learnreactivespring.handler;

import java.net.URI;
import net.shakya.learnreactivespring.constants.ItemConstants;
import net.shakya.learnreactivespring.document.Item;
import net.shakya.learnreactivespring.document.ItemCapped;
import net.shakya.learnreactivespring.repository.ItemReactiveCappedRepository;
import net.shakya.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ItemHandler {


  public final static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

  ItemReactiveRepository repository;
  ItemReactiveCappedRepository cappedRepository;

  public ItemHandler(ItemReactiveRepository repository,
      ItemReactiveCappedRepository cappedRepository) {
    this.repository = repository;
    this.cappedRepository = cappedRepository;
  }

  public Mono<ServerResponse> getAllItems(ServerRequest serverRequest){
    return ServerResponse
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(repository.findAll(), Item.class);
  }

  public Mono<ServerResponse> getOneItem(ServerRequest serverRequest){
    Mono<Item> itemMono = repository.findById(serverRequest.pathVariable("id"));

    return itemMono.log()
        .flatMap(item ->
            ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(item)))
        .switchIfEmpty(notFound);
  }

  public Mono<ServerResponse> createItem(ServerRequest serverRequest){
    Mono<Item> itemToBeCreated = serverRequest.bodyToMono(Item.class);

    return itemToBeCreated.flatMap(item -> repository.save(item))
        .flatMap(item ->
              ServerResponse.created(
                  URI.create(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 + "/" + item.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                  .body(BodyInserters.fromValue(item)));
  }

  public Mono<ServerResponse> deleteItem(ServerRequest serverRequest){
    String deletingItemId = serverRequest.pathVariable("id");
    Mono<Void> deletedItem = repository.deleteById(deletingItemId);

    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(deletedItem, Void.class);
  }

  public Mono<ServerResponse> updateItem(ServerRequest serverRequest){
    Mono<Item> itemToBeUpdated = repository.findById(serverRequest.pathVariable("id"));

    return itemToBeUpdated.log()
        .flatMap(item ->
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(serverRequest.bodyToMono(Item.class), Item.class))
        .switchIfEmpty(notFound);
  }


  public Mono<ServerResponse> itemsException(ServerRequest serverRequest){
    throw new RuntimeException("RuntimeException occurred");
  }

  public Mono<ServerResponse> itemStream(ServerRequest serverRequest) {
    return ServerResponse
        .ok()
        .contentType(MediaType.APPLICATION_STREAM_JSON)
        .body(cappedRepository.findItemsBy(), ItemCapped.class);

  }
}
