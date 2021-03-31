package com.learn2code.spring.kafka.config;

import com.learn2code.spring.kafka.model.Book;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.ContainerStoppingBatchErrorHandler;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.Map;

@Configuration
@EnableKafka
public class ConsumerConfig {
    private Logger logger = LoggerFactory.getLogger(ConsumerConfig.class);

    @Autowired
    private KafkaProperties properties;

    @Value("${spring.kafka.consumer.poll.timeout.ms}")
    private String pollTimeout;

    @Bean
    public DefaultKafkaConsumerFactory cf(KafkaProperties properties) {
        Map<String, Object> props = properties.buildConsumerProperties();
        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new StringDeserializer());
    }

    @Bean
    @ConditionalOnMissingBean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);
        factory.getContainerProperties().setPollTimeout(Long.parseLong(pollTimeout.trim()));
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        //factory.setBatchErrorHandler(new SeekToCurrentBatchErrorHandler());
        factory.setBatchErrorHandler(new ContainerStoppingBatchErrorHandler());
        return factory;
    }

    @Bean
    public DefaultKafkaProducerFactory pf(KafkaProperties properties) {
        Map<String, Object> props = properties.buildProducerProperties();
        try (StringSerializer serializer = new StringSerializer();
             Serializer jsonSerializer = new JsonSerde<>(Book.class)
                     .noTypeInfo().ignoreTypeHeaders().serializer()) {
            return new DefaultKafkaProducerFactory(props, serializer, jsonSerializer);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(pf(properties));
    }
}
