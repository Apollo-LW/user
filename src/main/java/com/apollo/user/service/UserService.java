package com.apollo.user.service;

import com.apollo.user.model.User;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> getUserById(final String userId);

    Mono<Boolean> updateUser(final Mono<User> userMono);

    Mono<Boolean> deleteUser(final String userId);
}
