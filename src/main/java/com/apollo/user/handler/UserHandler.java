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

/**
 * Main user API request handler, which handles basic API operations
 */
@Component
@RequiredArgsConstructor
public class UserHandler {

    /**
     * Main service which will handle user data operation {@link UserService}
     */
    private final UserService userService;

    /**
     * return all user Genders
     *
     * @param request an empty non null request
     *
     * @return a Flux of {@link Gender} Enum values
     */
    public @NotNull Mono<ServerResponse> getGenders(final ServerRequest request) {
        final Flux<Gender> genderFlux = Flux.fromArray(Gender.values());
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(genderFlux , Gender.class);
    }

    /**
     * return all user Types
     *
     * @param request an empty non null request
     *
     * @return a Flux of {@link UserType} Enum Values
     */
    public @NotNull Mono<ServerResponse> getUserTypes(final ServerRequest request) {
        final Flux<UserType> userTypeFlux = Flux.fromArray(UserType.values());
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userTypeFlux , UserType.class);
    }

    /**
     * Read User
     * <p>
     * return user based on a userId that is passed as a path variable {@link RoutingConstant#USER_ID}
     *
     * @param request the request with have the path variable in it
     *
     * @return a {@link User} if and only if it was found successfully and return a {@link ServerResponse#notFound()} status if it was not found
     * it will return a {@link ServerResponse#badRequest()} if the userId was Null or Empty
     */
    public @NotNull Mono<ServerResponse> getUserById(final ServerRequest request) {
        final String userId = request.pathVariable(RoutingConstant.USER_ID);
        final Mono<User> userMono = this.userService.getUserById(userId).flatMap(Mono::justOrEmpty);
        return userMono
                .flatMap(user -> ServerResponse.ok().body(user , User.class))
                .switchIfEmpty(ServerResponse.notFound().build())
                .doOnError(throwable -> ServerResponse.badRequest().body(throwable.getMessage() , String.class));
    }

    /**
     * Create a new user from the user object that will be sent as a body with the request
     *
     * @param request the request with the user in the body
     *
     * @return the created {@link User}
     * <p>
     * and return a {@link ServerResponse#badRequest()} if the user was not created successfully
     */
    public @NotNull Mono<ServerResponse> createUser(final ServerRequest request) {
        final Mono<User> userMono = request.bodyToMono(User.class);
        final Mono<User> createdUserMono = this.userService.createUser(userMono).flatMap(Mono::justOrEmpty);
        return createdUserMono
                .flatMap(createdUser -> ServerResponse.ok().body(createdUser , User.class))
                .switchIfEmpty(ServerResponse.badRequest().build())
                .doOnError(throwable -> ServerResponse.badRequest().body(throwable.getMessage() , String.class));
    }

    /**
     * Update User
     * <p>
     * return a boolean flag that indicate if the user was updated successfully or not
     * </p>
     * <p>
     * the user is passed with the body of the request
     *
     * @param request the request with the body that have the updated use in it
     *
     * @return {@link Boolean#TRUE} if and only if the user was updated successfully and return {@link Boolean#FALSE} if not
     * and it will return a {@link ServerResponse#badRequest()} if the user was Null
     */
    public @NotNull Mono<ServerResponse> updateUser(final ServerRequest request) {
        final Mono<User> userMono = request.bodyToMono(User.class);
        final Mono<Boolean> isUserUpdated = this.userService.updateUser(userMono);
        return userMono
                .flatMap(user -> ServerResponse.accepted().body(user , User.class))
                .switchIfEmpty(ServerResponse.badRequest().body("Something is not right with the request" , String.class))
                .doOnError(throwable -> ServerResponse.badRequest().body(throwable.getMessage() , String.class));
    }

    /**
     * Delete User
     * <p>
     * return a boolean flag that indicate if the user was deleted successfully or not, based on a userId that is passed as a path variable
     * {@link RoutingConstant#USER_ID}
     *
     * @param request the request that have the path variable in it
     *
     * @return {@link Boolean#TRUE} if and only if the user was deleted successfully, and return {@link Boolean#FALSE} if not and it will return a
     * {@link ServerResponse#badRequest()} if the userId was Null or Empty
     */
    public @NotNull Mono<ServerResponse> deleteUser(final ServerRequest request) {
        final String userId = request.pathVariable(RoutingConstant.USER_ID);
        final Mono<Boolean> isUserDeleted = this.userService.deleteUser(userId);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(isUserDeleted , Boolean.class);
    }
}
