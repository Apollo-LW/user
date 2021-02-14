package com.apollo.user.config;

import com.apollo.user.constants.RoutingConstants;
import com.apollo.user.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> route(UserHandler userHandler) {
        return RouterFunctions
                .route()
                .path(RoutingConstants.USER , routeFunctionBuilder ->
                        routeFunctionBuilder.nest(accept(MediaType.APPLICATION_JSON) ,
                                builder -> builder
                                        .GET(RoutingConstants.GENDER , userHandler::getGenders)
                                        .GET(RoutingConstants.TYPES , userHandler::getUserTypes)
                                        .GET(RoutingConstants.USER_ID , userHandler::getUserById)
                                        .PUT(userHandler::updateUser)
                                        .DELETE(RoutingConstants.USER_ID , userHandler::deleteUser)))
                .build();
    }
}
