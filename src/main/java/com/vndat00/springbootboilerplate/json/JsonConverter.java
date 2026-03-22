package com.vndat00.springbootboilerplate.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

public class JsonConverter {
    private JsonConverter() {
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        return "";
    }

    public static <D> D deserialize(String json, Class<D> clazz) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(json, clazz);
        } catch (JsonProcessingException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        return null;
    }
}
