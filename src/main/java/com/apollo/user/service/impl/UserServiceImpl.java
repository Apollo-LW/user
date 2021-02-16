package com.apollo.user.service.impl;

import com.apollo.user.kafka.KafkaService;
import com.apollo.user.model.User;
import com.apollo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${user.kafka.store}")
    private String userStateStoreName;
    private final KafkaService kafkaService;
    private ReadOnlyKeyValueStore<String, User> userStateStore;
    private final InteractiveQueryService interactiveQueryService;

    private ReadOnlyKeyValueStore<String, User> getUserStateStore() {
        if (this.userStateStore == null)
            this.userStateStore = interactiveQueryService.getQueryableStore(this.userStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.userStateStore;
    }

    @Override
    public Mono<Optional<User>> getUserById(final String userId) {
        Optional<User> userOptional = Optional.ofNullable(this.getUserStateStore().get(userId));
        if (userOptional.isEmpty()) return Mono.just(Optional.empty());
        User user = userOptional.get();
        if (!user.isActive()) return Mono.just(Optional.empty());
        return Mono.just(Optional.of(user));
    }

    @Override
    public Mono<Boolean> updateUser(final Mono<User> userMono) {
        return userMono.flatMap(user -> this.getUserById(user.getUserId()).flatMap(userOptional -> {
            if (userOptional.isEmpty()) return Mono.just(false);
            User updateUser = userOptional.get();
            updateUser.setGivenName(user.getGivenName());
            updateUser.setFamilyName(user.getFamilyName());
            updateUser.setUserType(user.getUserType());
            updateUser.setImageUrl(user.getImageUrl());
            updateUser.setGender(user.getGender());
            updateUser.setBirthDate(user.getBirthDate());
            return this.kafkaService.sendUserRecord(Mono.just(updateUser)).map(Optional::isPresent);
        }));
    }

    @Override
    public Mono<Boolean> deleteUser(final String userId) {
        return this.getUserById(userId).flatMap(userOptional -> {
            if (userOptional.isEmpty()) return Mono.just(false);
            User user = userOptional.get();
            user.setActive(false);
            return this.kafkaService.sendUserRecord(Mono.just(user)).map(Optional::isPresent);
        });
    }
}
