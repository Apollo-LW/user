package com.apollo.user.kafka;

import com.apollo.user.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@CommonsLog(topic = "Kafka Service")
public class KafkaService {

    private final KafkaReceiver<String , User> userKafkaReceiver;
    private final KafkaSender<String , User> userKafkaSender;
    @Value("${user.kafka.topic}")
    private String topicName;
    @Getter
    private ConnectableFlux<ServerSentEvent<User>> userEventPublisher;

    @PostConstruct
    public void init() {
        this.userEventPublisher = userKafkaReceiver
                .receive()
                .map(userRecord -> ServerSentEvent.builder(userRecord.value()).build())
                .publish();
        this.userEventPublisher.connect();
    }

    public Mono<Optional<User>> sendUserRecord(Mono<User> userMono) {
        return this.sendUserRecord(userMono , false);
    }

    public Mono<Optional<User>> sendUserRecord(Mono<User> userMono , boolean flag) {
        return userMono.flatMap(user -> this.userKafkaSender
                .send(Mono.just(SenderRecord.create(new ProducerRecord<String, User>(this.topicName , user.getUserId() , flag ? null : user) , 1)))
                .next()
                .doOnNext(log::info)
                .map(integerSenderResult -> integerSenderResult.exception() == null ? Optional.of(user) : Optional.empty()));
    }

}
