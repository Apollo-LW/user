package com.apollo.user.service;

import com.apollo.user.model.User;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> getUserById(String userId);
    Mono<String> getUserName(String userId);
    Mono<User> updateUser(User user);
    Mono<Boolean> deleteUser(String userId);
}
