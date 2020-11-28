package com.apollo.user.kafka;

import com.apollo.user.model.User;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class KafkaUserProcessor {

    @Value("${user.kafka.store}")
    String userStateStoreName;

    @Bean
    public Function<KStream<String , User>, KTable<String , User>> userProcessor() {
        return userRecord -> userRecord.groupByKey().reduce((user , v1) -> v1 , Materialized.as(this.userStateStoreName));
    }

}
