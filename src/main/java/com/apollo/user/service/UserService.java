package com.apollo.user.service;

import com.apollo.user.model.User;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> getUserById(String userId);

    Mono<Boolean> updateUser(Mono<User> userMono);

    Mono<Boolean> deleteUser(String userId);
}
