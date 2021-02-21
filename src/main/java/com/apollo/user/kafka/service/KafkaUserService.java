package com.apollo.user.kafka.service;

import com.apollo.user.model.User;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.Optional;

/**
 * Main class that handles producing new events into the user topic
 */
@Service
@RequiredArgsConstructor
public class KafkaUserService {

    @Value("${user.kafka.topic}")
    private String topicName;
    private final KafkaSender<String, User> userKafkaSender;

    /**
     * Sending a user event to the user topic
     *
     * @param userMono user event to produce
     *
     * @return an Optional of user based on if the event was produced successfully or not
     */
    public Mono<Optional<User>> sendUserRecord(final Mono<User> userMono) {
        return userMono.flatMap(user -> this.userKafkaSender
                .send(Mono.just(SenderRecord.create(new ProducerRecord<String, User>(this.topicName , user.getUserId() , user) , user.getUserId())))
                .next()
                .map(senderResult -> senderResult.exception() == null ? Optional.of(user) : Optional.empty()));
    }

}
