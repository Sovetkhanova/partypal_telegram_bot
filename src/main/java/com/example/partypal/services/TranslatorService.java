package com.example.partypal.services;

import com.example.partypal.configurations.FeignSupportConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "translator", url = "${microservice-url.translator.url}", configuration = FeignSupportConfig.class)
public interface TranslatorService {

    @Cacheable(cacheNames = "text")
    @PostMapping("/test")
    String translateText(@RequestParam(value = "code", required = false) String lang,
                         @RequestParam(value = "text") String text);

    @GetMapping("/test")
    String detectTextsLang(@RequestParam(value = "text") String text);
}
