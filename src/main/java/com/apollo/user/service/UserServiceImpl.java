package com.apollo.user.service;

import com.apollo.user.kafka.KafkaService;
import com.apollo.user.model.User;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final InteractiveQueryService interactiveQueryService;
    private final KafkaService kafkaService;
    @Value("${user.kafka.store}")
    private String userStateStoreName;
    private ReadOnlyKeyValueStore<String , User> userStateStore;

    private ReadOnlyKeyValueStore<String , User> getUserStateStore() {
        if(this.userStateStore == null) this.userStateStore = interactiveQueryService.getQueryableStore(this.userStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.userStateStore;
    }

    @Override
    public Mono<User> getUserById(String userId) {
        return Mono.just(this.getUserStateStore().get(userId));
    }

    @Override
    public Mono<String> getUserName(String userId) {
        User user = this.getUserStateStore().get(userId);
        if(user == null) return Mono.empty();
        return Mono.just(user.getGivenName() + " " + user.getFamilyName());
    }

    @Override
    public Mono<User> updateUser(User user) {
        User tmpUser = this.getUserStateStore().get(user.getUserId());
        if (tmpUser == null) return Mono.empty();
        return this.kafkaService.sendUserRecord(user).map(sentUser -> sentUser.orElse(tmpUser));
    }

    @Override
    public Mono<Boolean> deleteUser(String userId) {
        User tmpUser = this.getUserStateStore().get(userId);
        if (tmpUser == null) return Mono.empty();
        return this.kafkaService.sendUserRecord(tmpUser , true).map(Optional::isPresent);
    }

    public Flux<User> getAllUsers() {
        return Flux.just(getUserStateStore()).map(stringUserReadOnlyKeyValueStore -> stringUserReadOnlyKeyValueStore.all().next().value);
    }
}
