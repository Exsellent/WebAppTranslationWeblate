package org.example.webapplicationofthetranslationservice.controller;

import org.example.webapplicationofthetranslationservice.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranslationController {

    @Autowired
    private TranslationService translationService;

    @GetMapping("/translate")
    public String translate(@RequestParam String inputText,
                            @RequestParam String sourceLang,
                            @RequestParam String targetLang,
                            @RequestParam String ipAddress) {
        return translationService.translateText(ipAddress, inputText, sourceLang, targetLang);
    }
}
