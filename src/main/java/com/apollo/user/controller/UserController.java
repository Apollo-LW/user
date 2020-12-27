package com.apollo.user.controller;

import com.apollo.user.model.Gender;
import com.apollo.user.model.User;
import com.apollo.user.model.UserType;
import com.apollo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/genders")
    public Flux<Gender> genderFlux() {
        return Flux.fromArray(Gender.values());
    }

    @GetMapping("/types")
    public Flux<UserType> userTypeFlux() {
        return Flux.fromArray(UserType.values());
    }

    @GetMapping(value = "/{userId}")
    public Mono<User> getUserById(@PathVariable("userId") String userId) {
        return userService.getUserById(userId);
    }

    @GetMapping(value = "/name/{userId}")
    public Mono<String> getUserFullName(@PathVariable("userId") String userId) {
        return userService.getUserName(userId);
    }

    @PutMapping(value = "/")
    public Mono<Boolean> updateUser(@RequestBody Mono<User> user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{userId}")
    public Mono<Boolean> deleteUser(@PathVariable("userId") String userId) {
        return userService.deleteUser(userId);
    }

}
