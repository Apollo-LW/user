package com.apollo.user.kafka.processor;

import com.apollo.user.model.User;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * Main User Model processor
 * To handle streaming from the user Kafka topic
 */
@Service
public class UserProcessor {

    @Value("${user.kafka.store}")
    private String userStateStoreName;

    /**
     * User state processor, that reduce incoming events from the user topic into it's final state
     *
     * @return a {@link KTable} which is a key (the userId) and a value (the user) of the final state of the User
     */
    @Bean
    public Function<KStream<String, User>, KTable<String, User>> userStateProcessor() {
        return userRecord -> userRecord
                .groupByKey()
                .reduce((user , updatedUser) -> updatedUser ,
                        Materialized.as(this.userStateStoreName));
    }

}
