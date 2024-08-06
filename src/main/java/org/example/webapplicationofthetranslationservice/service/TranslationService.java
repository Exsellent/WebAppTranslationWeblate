package org.example.webapplicationofthetranslationservice.service;

public interface TranslationService {
    String translateText(String ipAddress, String inputText, String sourceLang, String targetLang);
}
