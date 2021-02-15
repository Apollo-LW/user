package com.apollo.user.kafka.processor;

import com.apollo.user.model.User;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserProcessor {

    @Value("${user.kafka.store}")
    String userStateStoreName;

    @Bean
    public Function<KStream<String, User>, KTable<String, User>> userProcessorState() {
        return userRecord -> userRecord
                .groupByKey()
                .reduce((user , updatedUser) -> updatedUser ,
                        Materialized.as(this.userStateStoreName));
    }

}
