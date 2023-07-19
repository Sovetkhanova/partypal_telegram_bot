package com.example.it32007telegram.configurations;

import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class Utf8StringHttpMessageConverter extends StringHttpMessageConverter {
    public Utf8StringHttpMessageConverter() {
        setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        setDefaultCharset(StandardCharsets.UTF_8);
    }
}
