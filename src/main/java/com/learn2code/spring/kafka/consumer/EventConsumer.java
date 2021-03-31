package com.learn2code.spring.kafka.consumer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.learn2code.spring.kafka.component.MessageBuilder;
import com.learn2code.spring.kafka.model.Book;
import com.learn2code.spring.kafka.producer.EventProducer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchAcknowledgingConsumerAwareMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class EventConsumer implements BatchAcknowledgingConsumerAwareMessageListener<String, String> {
    private Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private MessageBuilder builder;

    @Override
    @KafkaListener(topics = "#{'${spring.kafka.consumer.topics}'.split(',')}")
    public void onMessage(List<ConsumerRecord<String, String>> records, Acknowledgment acknowledgment,
                          Consumer<?, ?> consumer) {
        List<Book> requestCollection = new ArrayList<>();

        eventProducer.setActive(true);
        logger.info("No of records consumed for following batch: {}", records.size());

        for (ConsumerRecord<String, String> record : records) {
            logger.info("Consumed record:: Key: {}, Topic: {}, Offset: {}", record.key(), record.topic(), record.offset());

            if (record.value() != null) {
                try {
                    requestCollection.add(MessageBuilder.convertToObject(record.value()));
                } catch (JsonProcessingException e) {
                    logger.info("Record {}, Error {}", record.value(), e.getMessage());
                }
            }
        }

        if (requestCollection.size() > 0) {
            eventProducer.sendToKafka(requestCollection);
        }

        if (eventProducer.isActive()) {
            logger.info("Committing offset");
            acknowledgment.acknowledge();
            consumer.commitSync();
        } else {
            throw new RuntimeException("Error in Producer - failing consumer");
        }

    }
}
