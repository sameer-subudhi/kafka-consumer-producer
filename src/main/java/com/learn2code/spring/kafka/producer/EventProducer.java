package com.learn2code.spring.kafka.producer;


import com.learn2code.spring.kafka.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;

@Component
public class EventProducer {
    private Logger logger = LoggerFactory.getLogger(EventProducer.class);

    @Value("${spring.application.name}")
    private String appName;
    @Value("${spring.application.version}")
    private String appVersion;

    @Value("${spring.kafka.producer.topics.output}")
    private String requestTopic;
    @Value("${spring.kafka.producer.topics.error}")
    private String errorTopic;

    private boolean active;

    @Autowired
    private KafkaTemplate template;

    public void sendToKafka(List<Book> requestMessageList) {
        for (Book message : requestMessageList) {
            if (message.isGood()) {
                ListenableFuture<SendResult<String, Object>> listenableFuture = template.send(requestTopic, null, message);
                listenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
                    @Override
                    public void onSuccess(SendResult<String, Object> result) {
                        logger.info("Record: {}", message);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        logger.info("Message: {}, Error {}", message, throwable.getMessage());
                        setActive(false);
                    }
                });
            } else {
                ListenableFuture<SendResult<String, Object>> listenableFuture = template.send(errorTopic, null, message);
                listenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
                    @Override
                    public void onSuccess(SendResult<String, Object> result) {
                        logger.info("Record: {}", message);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        logger.info("Message: {}, Error {}", message, throwable.getMessage());
                        setActive(false);
                    }
                });
            }
        }

        template.flush();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
