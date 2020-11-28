package com.apollo.user.controller;

import com.apollo.user.model.Gender;
import com.apollo.user.model.User;
import com.apollo.user.model.UserType;
import com.apollo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final static Random random = new Random();

    private String getRandomEle(@org.jetbrains.annotations.NotNull List<String> list) {
        return list.get(random.nextInt(list.size()));
    }

    @GetMapping("/genders")
    public Flux<Gender> genderFlux() {
        return Flux.fromArray(Gender.values());
    }

    @GetMapping("/types")
    public Flux<UserType> userTypeFlux() {
        return Flux.fromArray(UserType.values());
    }

    @GetMapping("/all")
    public Flux<User> getAllUsers() { //TODO: This is just for testing purposes
        return this.userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public Mono<User> getUserById(@PathVariable("userId") String userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/name/{userId}")
    public Mono<String> getUserFullName(@PathVariable("userId") String userId) {
        return userService.getUserName(userId);
    }

    @PutMapping("/")
    public Mono<User> updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{userId}")
    public Mono<Boolean> deleteUser(@PathVariable("userId") String userId) {
        return userService.deleteUser(userId);
    }

}
