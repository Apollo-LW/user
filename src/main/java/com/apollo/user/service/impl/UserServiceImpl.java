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
    private final InteractiveQueryService interactiveQueryService;
    private final KafkaService kafkaService;
    private ReadOnlyKeyValueStore<String, User> userStateStore;

    private ReadOnlyKeyValueStore<String, User> getUserStateStore() {
        if (this.userStateStore == null)
            this.userStateStore = interactiveQueryService.getQueryableStore(this.userStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.userStateStore;
    }

    @Override
    public Mono<User> getUserById(String userId) {
        Optional<User> userOptional = Optional.ofNullable(this.getUserStateStore().get(userId));
        if (userOptional.isEmpty()) return Mono.empty();
        User user = userOptional.get();
        if (!user.isActive()) return Mono.empty();
        return Mono.just(user);
    }

    @Override
    public Mono<String> getUserName(String userId) {
        Optional<User> userOptional = Optional.ofNullable(this.getUserStateStore().get(userId));
        if (userOptional.isEmpty()) return Mono.empty();
        User user = userOptional.get();
        return Mono.just(user.getGivenName() + " " + user.getFamilyName());
    }

    @Override
    public Mono<Boolean> updateUser(Mono<User> userMono) {
        return userMono.flatMap(user -> {
            Optional<User> userOptional = Optional.ofNullable(this.getUserStateStore().get(user.getUserId()));
            if (userOptional.isEmpty()) return Mono.empty();
            User updateUser = userOptional.get();
            updateUser.setGivenName(user.getGivenName());
            updateUser.setFamilyName(user.getFamilyName());
            updateUser.setUserType(user.getUserType());
            updateUser.setImageUrl(user.getImageUrl());
            updateUser.setGender(user.getGender());
            updateUser.setBirthDate(user.getBirthDate());
            return this.kafkaService.sendUserRecord(Mono.just(updateUser)).map(Optional::isPresent);
        });
    }

    @Override
    public Mono<Boolean> deleteUser(String userId) {
        Optional<User> userOptional = Optional.ofNullable(this.getUserStateStore().get(userId));
        if (userOptional.isEmpty()) return Mono.empty();
        User user = userOptional.get();
        user.setActive(false);
        return this.kafkaService.sendUserRecord(Mono.just(user)).map(Optional::isPresent);
    }
}
