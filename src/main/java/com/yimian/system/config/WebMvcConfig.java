package com.yimian.system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Web MVC 全局配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /** 统一日期时间格式：yyyy-MM-dd HH:mm:ss（不带 T） */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";

    /**
     * 全局 ObjectMapper — 支持 Java 8 时间 API，统一格式化为 yyyy-MM-dd HH:mm:ss
     *
     * spring.jackson.date-format 只对 java.util.Date 生效，对 LocalDateTime 无效；
     * LocalDateTime 的格式由 JavaTimeModule 的序列化器决定，默认输出 ISO-8601（带 T）。
     * 这里通过 SimpleModule 注册带 DateTimeFormatter 的序列化/反序列化器，统一去除 T。
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN);
        DateTimeFormatter tf = DateTimeFormatter.ofPattern(TIME_PATTERN);

        SimpleModule timeModule = new SimpleModule();
        // 序列化（对象 → JSON）
        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dtf));
        timeModule.addSerializer(LocalDate.class, new LocalDateSerializer(df));
        timeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(tf));
        // 反序列化（JSON → 对象）
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dtf));
        timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(df));
        timeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(tf));
        timeModule.addSerializer(Long.class, ToStringSerializer.instance);
        timeModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        mapper.registerModule(timeModule);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
