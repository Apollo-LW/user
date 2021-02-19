package com.apollo.user.config;

import com.apollo.user.constant.RoutingConstant;
import com.apollo.user.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

/**
 * Configuration for the router functions, to be able to route request to the correct handler
 */
@Configuration
public class RouterConfig {

    /**
     * Main routing function for the User API
     * Base URI will start with {@link RoutingConstant#USER_PATH}
     * and accept JSON Data for all request
     *
     * @param userHandler the main handler for user operations
     *
     * @return a {@link RouterFunction} with the handler functions return type, which is a ServerResponse in this case
     */
    @Bean
    public RouterFunction<ServerResponse> route(final UserHandler userHandler) {
        return RouterFunctions
                .route()
                .path(RoutingConstant.USER_PATH , routeFunctionBuilder ->
                        routeFunctionBuilder.nest(accept(MediaType.APPLICATION_JSON) ,
                                builder -> builder
                                        .GET(RoutingConstant.GENDER , userHandler::getGenders)
                                        .GET(RoutingConstant.TYPES , userHandler::getUserTypes)
                                        .GET(RoutingConstant.USER_ID_PATH , userHandler::getUserById)
                                        .PUT(userHandler::updateUser)
                                        .DELETE(RoutingConstant.USER_ID_PATH , userHandler::deleteUser)))
                .build();
    }
}
