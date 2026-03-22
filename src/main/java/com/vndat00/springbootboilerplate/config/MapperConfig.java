package com.vndat00.springbootboilerplate.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class MapperConfig {

  @Bean
  @Primary
  ObjectMapper snakeCaseObjectMapper() {
    var mapper = new ObjectMapper();

    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModules(new JavaTimeModule());
    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    return mapper;
  }

  @Bean("CamelCaseObjectMapper")
  ObjectMapper camelCaseObjectMapper() {
    var mapper = new ObjectMapper();

    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModules(new JavaTimeModule());
    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);

    return mapper;
  }
}
