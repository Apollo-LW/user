package com.apollo.user.handler;

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

    public @NotNull Mono<ServerResponse> getGenders(ServerRequest request) {
        Flux<Gender> genderFlux = Flux.fromArray(Gender.values());
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(genderFlux , Gender.class);
    }

    public @NotNull Mono<ServerResponse> getUserTypes(ServerRequest request) {
        Flux<UserType> userTypeFlux = Flux.fromArray(UserType.values());
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userTypeFlux , UserType.class);
    }

    public @NotNull Mono<ServerResponse> getUserById(ServerRequest request) {
        final String userId = request.pathVariable("userId");
        Mono<User> userMono = this.userService.getUserById(userId);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userMono , User.class);
    }

    public @NotNull Mono<ServerResponse> updateUser(ServerRequest request) {
        Mono<User> userMono = request.bodyToMono(User.class);
        Mono<Boolean> updateUserStatus = this.userService.updateUser(userMono);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateUserStatus , Boolean.class);
    }

    public @NotNull Mono<ServerResponse> deleteUser(ServerRequest request) {
        String userId = request.pathVariable("userId");
        Mono<Boolean> deleteUserStatus = this.userService.deleteUser(userId);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(deleteUserStatus , Boolean.class);
    }
}
