package com.project.demo.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
public class DateTimeConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomizer() {
        return builder -> builder.timeZone(TimeZone.getTimeZone("UTC"));
    }

    @Bean
    public java.time.Clock clock() {
        return java.time.Clock.systemUTC();
    }
}