package com.apollo.user.kafka;

import com.apollo.user.model.User;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaReceiver<String , User> userKafkaReceiver;
    private final KafkaSender<String , User> userKafkaSender;
    @Value("${user.kafka.topic}")
    private String topicName;

    public Mono<Optional<User>> sendUserRecord(Mono<User> userMono) {
        return userMono.flatMap(user -> this.userKafkaSender
                .send(Mono.just(SenderRecord.create(new ProducerRecord<String, User>(this.topicName , user.getUserId() , user) , 1)))
                .next()
                .map(integerSenderResult -> integerSenderResult.exception() == null ? Optional.of(user) : Optional.empty()));
    }

}
