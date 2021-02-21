package com.apollo.user.kafka.config;

import com.apollo.user.model.User;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
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

import java.util.Properties;

/**
 * Main User Kafka Configuration which
 * <ul>
 *     <li>Topic Creation</li>
 *     <li>Reactive Producer Configuration</li>
 *     <li>Reactive Receiver Configuration</li>
 * </ul>
 */
@Configuration
public class KafkaConfiguration {

    @Value("${user.kafka.server}")
    private String bootstrapServer;
    @Value("${user.kafka.topic}")
    private String topicName;
    @Value("${user.kafka.partition}")
    private Integer numberOfPartitions;
    @Value("${user.kafka.replicas}")
    private Short numberOfReplicas;
    @Value("${user.kafka.retention}")
    private String retentionPeriod;
    @Value("${user.kafka.acks}")
    private String numberOfAcks;
    @Value("${user.kafka.retries}")
    private Integer numberOfRetries;
    @Value("${user.kafka.requestimeout}")
    private String requestTimeout;
    @Value("${user.kafka.batch}")
    private String batchSize;
    @Value("${user.kafka.linger}")
    private String linger;
    @Value("${user.kafka.max-in-flight}")
    private String maxInFlight;
    @Value("${user.kafka.client-id}")
    private String clientId;
    @Value("${user.kafka.group-id}")
    private String groupId;
    @Value("${user.kafka.offset}")
    private String offset;

    /**
     * User topic creation
     *
     * @return a new Kafka Topic {@link NewTopic} if it was not created in the topic
     */
    @Bean
    NewTopic createUserTopic() {
        return TopicBuilder
                .name(this.topicName)
                .partitions(this.numberOfPartitions)
                .replicas(this.numberOfReplicas)
                .config(TopicConfig.RETENTION_MS_CONFIG , this.retentionPeriod)
                .build();
    }

    /**
     * Reactive Producer Configuration
     *
     * @return a new Reactive Producer {@link KafkaSender}
     */
    @Bean
    KafkaSender<String, User> userKafkaSender() {
        final Properties userSenderProperties = new Properties();
        userSenderProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG , this.bootstrapServer);
        userSenderProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG , StringSerializer.class);
        userSenderProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG , JsonSerializer.class);
        userSenderProperties.put(ProducerConfig.ACKS_CONFIG , this.numberOfAcks);
        userSenderProperties.put(ProducerConfig.RETRIES_CONFIG , this.numberOfRetries);
        userSenderProperties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG , this.requestTimeout);
        userSenderProperties.put(ProducerConfig.BATCH_SIZE_CONFIG , this.batchSize);
        userSenderProperties.put(ProducerConfig.LINGER_MS_CONFIG , this.linger);
        userSenderProperties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION , this.maxInFlight);

        return new DefaultKafkaSender<String, User>(ProducerFactory.INSTANCE , SenderOptions.create(userSenderProperties));
    }

    /**
     * Reactive Consumer Configuration
     *
     * @return a new Reactive Consumer {@link KafkaReceiver}
     */
    @Bean
    KafkaReceiver<String, User> userKafkaReceiver() {
        final Properties userReceiverProperties = new Properties();
        userReceiverProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG , this.bootstrapServer);
        userReceiverProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG , StringDeserializer.class);
        userReceiverProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG , JsonDeserializer.class);
        userReceiverProperties.put(ConsumerConfig.CLIENT_ID_CONFIG , this.clientId);
        userReceiverProperties.put(ConsumerConfig.GROUP_ID_CONFIG , this.groupId);
        userReceiverProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG , true);
        userReceiverProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG , this.offset);

        return new DefaultKafkaReceiver<String, User>(ConsumerFactory.INSTANCE , ReceiverOptions.create(userReceiverProperties));
    }
}
