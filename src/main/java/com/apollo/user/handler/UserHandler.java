package com.apollo.user.handler;

import com.apollo.user.constant.RoutingConstant;
import com.apollo.user.model.Gender;
import com.apollo.user.model.User;
import com.apollo.user.model.UserType;
import com.apollo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private final UserService userService;

    public @NotNull Mono<ServerResponse> getGenders(final ServerRequest request) {
        final Flux<Gender> genderFlux = Flux.fromArray(Gender.values());
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(genderFlux , Gender.class);
    }

    public @NotNull Mono<ServerResponse> getUserTypes(final ServerRequest request) {
        final Flux<UserType> userTypeFlux = Flux.fromArray(UserType.values());
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userTypeFlux , UserType.class);
    }

    public @NotNull Mono<ServerResponse> getUserById(final ServerRequest request) {
        final String userId = request.pathVariable(RoutingConstant.USER_ID);
        final Mono<User> userMono = this.userService.getUserById(userId);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userMono , User.class);
    }

    public @NotNull Mono<ServerResponse> updateUser(final ServerRequest request) {
        final Mono<User> userMono = request.bodyToMono(User.class);
        final Mono<Boolean> isUserUpdated = this.userService.updateUser(userMono);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(isUserUpdated , Boolean.class);
    }

    public @NotNull Mono<ServerResponse> deleteUser(final ServerRequest request) {
        final String userId = request.pathVariable(RoutingConstant.USER_ID);
        final Mono<Boolean> isUserDeleted = this.userService.deleteUser(userId);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(isUserDeleted , Boolean.class);
    }
}
