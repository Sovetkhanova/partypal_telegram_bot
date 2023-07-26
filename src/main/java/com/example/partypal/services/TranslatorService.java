package com.example.partypal.services;

import com.example.partypal.configurations.FeignSupportConfig;
import com.example.partypal.models.enums.Lang;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "translator", url = "${microservice-url.translator.url}", configuration = FeignSupportConfig.class)
public interface TranslatorService {
    @PostMapping("/test")
    String translateText(@RequestParam(value = "code", required = false) Lang lang,
                         @RequestParam(value = "text") String text);
    @GetMapping("/test")
    Object detectTextsLang(@RequestBody String text);
}
