package com.apollo.user.kafka;

import com.apollo.user.model.User;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.internals.ConsumerFactory;
import reactor.kafka.receiver.internals.DefaultKafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.internals.DefaultKafkaSender;
import reactor.kafka.sender.internals.ProducerFactory;

import java.util.Collections;
import java.util.Properties;

@Configuration
@CommonsLog(topic = "Kafka Config")
public class KafkaConfiguration {

    @Value("${user.kafka.server}")
    private String bootstrapServer;
    @Value("${user.kafka.topic}")
    private String topicName;
    @Value("${user.kafka.partition}")
    private Integer numberOfPartitions;
    @Value("${user.kafka.replicas}")
    private Short numberOfReplicas;

    @Bean
    public NewTopic createUserTopic() {
        return new NewTopic(this.topicName , this.numberOfPartitions , this.numberOfReplicas);
    }

    @Bean
    KafkaSender<String , User> userKafkaSender() {
        final Properties userProducerProperties = new Properties();
        userProducerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG , this.bootstrapServer);
        userProducerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG , StringSerializer.class);
        userProducerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG , JsonSerializer.class);
        userProducerProperties.put(ProducerConfig.ACKS_CONFIG , "all");
        userProducerProperties.put(ProducerConfig.RETRIES_CONFIG , 10);
        userProducerProperties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000");
        userProducerProperties.put(ProducerConfig.BATCH_SIZE_CONFIG, "163850");
        userProducerProperties.put(ProducerConfig.LINGER_MS_CONFIG, "100");
        userProducerProperties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "1");

        return new DefaultKafkaSender<String , User>(ProducerFactory.INSTANCE , SenderOptions.create(userProducerProperties));
    }

    @Bean
    KafkaReceiver<String , User> userKafkaReceiver() {
        final Properties userReceiverProperties = new Properties();
        userReceiverProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG , this.bootstrapServer);
        userReceiverProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG , StringDeserializer.class);
        userReceiverProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG , JsonDeserializer.class);
        userReceiverProperties.put(ConsumerConfig.CLIENT_ID_CONFIG , "user-client");
        userReceiverProperties.put(ConsumerConfig.GROUP_ID_CONFIG , "user-group");
        userReceiverProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG , true);
        userReceiverProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG , "latest");
        ReceiverOptions<String , User> userReceiverOptions = ReceiverOptions.create(userReceiverProperties);
        return new DefaultKafkaReceiver<String , User>(ConsumerFactory.INSTANCE , userReceiverOptions.subscription(Collections.singleton(this.topicName)));
    }
}
