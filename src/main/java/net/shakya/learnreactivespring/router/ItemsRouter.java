package net.shakya.learnreactivespring.router;

import net.shakya.learnreactivespring.constants.ItemConstants;
import net.shakya.learnreactivespring.handler.ItemHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ItemsRouter {

  @Bean
  public RouterFunction<ServerResponse> itemsRoute(ItemHandler itemHandler){
    return RouterFunctions.route(
          RequestPredicates.GET(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
            .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                itemHandler::getAllItems)
        .andRoute(
            RequestPredicates.GET(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}")
            .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                itemHandler::getOneItem)
        .andRoute(
            RequestPredicates.POST(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                  itemHandler::createItem)
        .andRoute(
            RequestPredicates.DELETE(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                  itemHandler::deleteItem)
        .andRoute(
          RequestPredicates.PUT(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}")
              .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                  itemHandler::updateItem);
  }

  @Bean
  public RouterFunction<ServerResponse> errorRoute(ItemHandler itemHandler){
    return RouterFunctions.route(
        RequestPredicates.GET(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 + "_runtimeExp")
            .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
        itemHandler::itemsException);
  }

  @Bean
  public RouterFunction<ServerResponse> itemStreamRoute(ItemHandler itemHandler){
    return RouterFunctions.route(
        RequestPredicates.GET(ItemConstants.ITEM_STREAM_FUNCTIONAL_END_POINT_V1)
            .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
        itemHandler::itemStream);
  }
}
