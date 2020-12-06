package com.apollo.user.controller;

import com.apollo.user.model.Gender;
import com.apollo.user.model.User;
import com.apollo.user.model.UserType;
import com.apollo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
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

    private String getRandomEle(@NotNull List<String> list) {
        return list.get(random.nextInt(list.size()));
    }

    @GetMapping
    public Mono<User> createUser() {
        final List<String> names = Arrays.asList("Mohammad" , "Ali" , "Ahmed" , "Laith" , "Ibrahim");
        final List<String> usernames = Arrays.asList("MrMoon" , "MrSeal" , "MrSeal Jr" , "Raptor" , "xXxX");
        final List<String> images = new ArrayList<>();
        images.add("https://i.pinimg.com/originals/af/8d/63/af8d63a477078732b79ff9d9fc60873f.jpg");
        images.add("https://lumiere-a.akamaihd.net/v1/images/sa_pixar_virtualbg_coco_16x9_9ccd7110.jpeg?region=0,0,1920,1080");
        images.add("https://userpic.codeforces.com/844466/title/364d33767b1f3586.jpg");
        User user = new User();
        user.setUserType(Math.random() > 0.5 ? UserType.STUDENT : UserType.TEACHER);
        user.setGender(Math.random() > 0.5 ? Gender.FEMALE : Gender.MALE);
        user.setImageUrl(getRandomEle(images));
        user.setFamilyName(getRandomEle(names));
        user.setGivenName(getRandomEle(names));
        user.setUsername(getRandomEle(usernames));
        user.setBirthDate(Calendar.getInstance().getTime());
        user.setAuthTime(Calendar.getInstance().getTime());
        user.setExpiresIn(Calendar.getInstance().getTime());
        user.setIssuedAt(Calendar.getInstance().getTime());
        user.setEmail(user.getUsername() + user.getUserId().substring(0 , 5) + "@gmail.com");
        return userService.createUser(Mono.just(user));
    }

    @GetMapping("/genders")
    public Flux<Gender> genderFlux() {
        return Flux.fromArray(Gender.values());
    }

    @GetMapping("/types")
    public Flux<UserType> userTypeFlux() {
        return Flux.fromArray(UserType.values());
    }

    @GetMapping(value = "/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<User> getUserById(@PathVariable("userId") String userId) {
        return userService.getUserById(userId);
    }

    @GetMapping(value = "/name/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<String> getUserFullName(@PathVariable("userId") String userId) {
        return userService.getUserName(userId);
    }

    @PutMapping(value = "/", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<User> updateUser(@RequestBody Mono<User> user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{userId}")
    public Mono<Boolean> deleteUser(@PathVariable("userId") String userId) {
        return userService.deleteUser(userId);
    }

}
