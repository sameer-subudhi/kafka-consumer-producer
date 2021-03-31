package com.learn2code.spring.kafka.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn2code.spring.kafka.model.Book;
import org.springframework.stereotype.Component;

@Component
public class MessageBuilder {

    public static Book convertToObject(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Book.class);
    }
}
