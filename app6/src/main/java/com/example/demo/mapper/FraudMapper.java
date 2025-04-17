package com.example.demo.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FraudMapper {
    // message
    public static <T> String toMessage(T t) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize account response", e);
        }
    }


    public static <T> T parseMessage(String jsonStrMessage, TypeReference<T> typeReference) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonStrMessage, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse message", e);
        }
    }
}
