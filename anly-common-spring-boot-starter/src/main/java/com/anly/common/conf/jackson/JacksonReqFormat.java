package com.anly.common.conf.jackson;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * todo-anlythree 貌似没什么用，应该是要用objectMapper配置
 */
@Configuration
public class JacksonReqFormat {
    /**
     * Date格式化字符串
     */
    private static final String DATE_FORMAT = DatePattern.NORM_DATE_PATTERN; //yyyy-MM-dd
    /**
     * DateTime格式化字符串
     */
    private static final String DATETIME_FORMAT = DatePattern.NORM_DATETIME_PATTERN; //yyyy-MM-dd HH:mm:ss
    /**
     * Time格式化字符串
     */
    private static final String TIME_FORMAT = DatePattern.NORM_TIME_PATTERN; //HH:mm:ss

    /**
     * Jackson全局转化long类型为String
     * 解决jackson序列化时传入前端Long类型缺失精度问题
     * 解决LocalDateTime中间带T的问题
     * @return
     */
    @Bean
    @Primary
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder
                .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT)))
                .serializerByType(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT)))
                .serializerByType(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(TIME_FORMAT)))
                .serializerByType(BigInteger.class, ToStringSerializer.instance)
                .serializerByType(BigDecimal.class, ToStringSerializer.instance)
                .serializerByType(Long.class, ToStringSerializer.instance)
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT)))
                .deserializerByType(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT)))
                .deserializerByType(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(TIME_FORMAT)))
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

}
