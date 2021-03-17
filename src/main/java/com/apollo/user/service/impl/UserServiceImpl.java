package com.apollo.user.service.impl;

import com.apollo.user.constant.ErrorConstant;
import com.apollo.user.kafka.service.KafkaUserService;
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

/**
 * Main User Service which handle tasks such as
 * <ul>
 *     <li>Reading a User</li>
 *     <li>Updating a User</li>
 *     <li>Deleting a User</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${user.kafka.store}")
    private String userStateStoreName;
    private final KafkaUserService kafkaUserService;
    private ReadOnlyKeyValueStore<String, User> userStateStore;
    private final InteractiveQueryService interactiveQueryService;

    /**
     * Getting the main user state store to be able to read values from it
     *
     * @return a {@link ReadOnlyKeyValueStore} state store with the userId as the key and the user object as the value
     */
    private ReadOnlyKeyValueStore<String, User> getUserStateStore() {
        if (this.userStateStore == null)
            this.userStateStore = interactiveQueryService.getQueryableStore(this.userStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.userStateStore;
    }

    /**
     * Reading the user from the state store based on the key
     *
     * @param userId the key to get the user object from the state store
     *
     * @return an Optional based on if the user object was found in the state store or not, we throw a {@link NullPointerException} if the userId was Null,
     * and an {@link IllegalArgumentException} if the userId was Empty
     */
    @Override
    public Mono<Optional<User>> getUserById(final String userId) {
        if (userId == null)
            return Mono.error(new NullPointerException(ErrorConstant.USER_ID_NULL));
        if (userId.length() == 0)
            return Mono.error(new IllegalAccessError(ErrorConstant.USER_ID_EMPTY));

        Optional<User> userOptional = Optional.ofNullable(this.getUserStateStore().get(userId));
        if (userOptional.isEmpty()) return Mono.just(Optional.empty());
        User user = userOptional.get();
        if (!user.isActive()) return Mono.just(Optional.empty());
        return Mono.just(Optional.of(user));
    }

    /**
     * Update user object by producing the new user object into the topic
     *
     * @param userMono the new user with the updated values, notice that the ID must not change so we can find the old user
     *
     * @return a boolean flag if the user was updated successfully or not, we throw a {@link NullPointerException} if the user is Null
     */
    @Override
    public Mono<Boolean> updateUser(final Mono<User> userMono) {
        return userMono.flatMap(user -> {
            if (user == null)
                return Mono.error(new NullPointerException(ErrorConstant.UPDATE_USER));

            return this.getUserById(user.getUserId()).flatMap(userOptional -> {
                if (userOptional.isEmpty()) return Mono.just(false);
                User updateUser = userOptional.get();
                updateUser.setGivenName(user.getGivenName());
                updateUser.setFamilyName(user.getFamilyName());
                updateUser.setUserType(user.getUserType());
                updateUser.setImageUrl(user.getImageUrl());
                updateUser.setGender(user.getGender());
                updateUser.setBirthDate(user.getBirthDate());
                return this.kafkaUserService.sendUserRecord(Mono.just(updateUser)).map(Optional::isPresent);
            });
        });
    }

    /**
     * Delete the user by changing the value of the {@link User#isActive()} flag to false
     *
     * @param userId the userId of the the user to be deleted
     *
     * @return a boolean flag if the user was deleted successfully or not
     *
     * @throws NullPointerException     if the userId was Null
     * @throws IllegalArgumentException if the userId was Empty
     */
    @Override
    public Mono<Boolean> deleteUser(final String userId) {
        if (userId == null)
            return Mono.error(new NullPointerException(ErrorConstant.USER_ID_NULL));
        if (userId.length() == 0)
            return Mono.error(new IllegalArgumentException(ErrorConstant.USER_ID_EMPTY));

        return this.getUserById(userId).flatMap(userOptional -> {
            if (userOptional.isEmpty()) return Mono.just(false);
            User user = userOptional.get();
            user.setActive(false);
            return this.kafkaUserService.sendUserRecord(Mono.just(user)).map(Optional::isPresent);
        });
    }

    /**
     * Create a user event
     *
     * @param userMono the user that will be created
     *
     * @return an {@link Optional} of a {@link User} so we don't deal with null values directly
     */
    @Override
    public Mono<Optional<User>> createUser(Mono<User> userMono) {
        return this.kafkaUserService.sendUserRecord(userMono);
    }
}
