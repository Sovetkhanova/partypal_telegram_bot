package com.example.partypal.configurations;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@Configuration
public class FeignSupportConfig {

    @Bean
    public Encoder feignEncoder() {
        return new SpringEncoder(feignHttpMessageConverter());
    }

    @Bean
    public Decoder feignDecoder() {
        return new Utf8ResponseEntityDecoder(feignHttpMessageConverter());
    }

    private ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
        final HttpMessageConverters httpMessageConverters = new HttpMessageConverters(new Utf8StringHttpMessageConverter());
        return () -> httpMessageConverters;
    }

    private static class Utf8StringHttpMessageConverter extends org.springframework.http.converter.StringHttpMessageConverter {
        private static final org.springframework.http.MediaType UTF8_MEDIA_TYPE = new org.springframework.http.MediaType("text", "plain", StandardCharsets.UTF_8);

        @Override
        protected org.springframework.http.MediaType getDefaultContentType(@NotNull String dumy) {
            return UTF8_MEDIA_TYPE;
        }

    }

    private static class Utf8ResponseEntityDecoder implements Decoder {
        private final ObjectMapper objectMapper;

        public Utf8ResponseEntityDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
            new ResponseEntityDecoder(new SpringDecoder(messageConverters));
            this.objectMapper = new ObjectMapper().configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        }

        @Override
        public Object decode(Response response, Type type) throws IOException {
            try (Response.Body body = response.body()) {
                if (body == null) {
                    return null;
                }
                String content = Util.toString(body.asReader(StandardCharsets.UTF_8));
                return objectMapper.readValue(content, objectMapper.constructType(type));
            }
        }
    }
}
