package org.example.webapplicationofthetranslationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.example.webapplicationofthetranslationservice.exception.TranslationApiException;
import org.example.webapplicationofthetranslationservice.exception.InvalidLanguageException;
import org.example.webapplicationofthetranslationservice.controller.TranslationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class TranslationServiceImpl implements TranslationService {

    private static final int MAX_THREADS = 10;
    private static final Logger log = LoggerFactory.getLogger(TranslationServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${translation.api.url}")
    private String translationApiUrl;

    @Value("${translation.api.key}")
    private String apiKey;

    private final ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);
    private final Map<String, String> translationCache = new ConcurrentHashMap<>();

    @Override
    public String translateText(String ipAddress, String inputText, String sourceLang, String targetLang) {
        validateInput(inputText, sourceLang, targetLang);

        List<String> words = Arrays.asList(inputText.split("\\s+"));
        List<CompletableFuture<String>> futures = words.stream()
                .map(word -> CompletableFuture.supplyAsync(() -> translateWord(word, sourceLang, targetLang), executorService))
                .collect(Collectors.toList());

        String translatedText = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.joining(" "));

        saveTranslationRequest(ipAddress, inputText, translatedText);
        return translatedText;
    }

    private String translateWord(String word, String sourceLang, String targetLang) {
        String cacheKey = sourceLang + ":" + targetLang + ":" + word;
        return translationCache.computeIfAbsent(cacheKey, k -> {
            try {
                String url = String.format("%s?key=%s&text=%s&lang=%s-%s",
                        translationApiUrl, apiKey, URLEncoder.encode(word, StandardCharsets.UTF_8), sourceLang, targetLang);
                TranslationResponse response = restTemplate.getForObject(url, TranslationResponse.class);
                if (response != null && !response.getText().isEmpty()) {
                    return response.getText().get(0);
                } else {
                    throw new TranslationApiException("Translation API returned an empty response");
                }
            } catch (Exception e) {
                log.error("Translation error for word: " + word, e);
                return word; // Возвращаем оригинальное слово в случае ошибки
            }
        });
    }

    private void validateInput(String inputText, String sourceLang, String targetLang) {
        if (inputText == null || inputText.isEmpty()) {
            throw new IllegalArgumentException("Input text cannot be empty");
        }
        if (sourceLang == null || targetLang == null) {
            throw new IllegalArgumentException("Source and target languages must be specified");
        }
        // Дополнительные проверки для языков
        if (!isValidLanguage(sourceLang) || !isValidLanguage(targetLang)) {
            throw new InvalidLanguageException("Unsupported language specified");
        }
    }

    private boolean isValidLanguage(String lang) {

        return true;
    }

    private void saveTranslationRequest(String ipAddress, String inputText, String translatedText) {
        jdbcTemplate.update(
                "INSERT INTO translation_requests (ip_address, input_text, translated_text, request_time) VALUES (?, ?, ?, ?)",
                ipAddress, inputText, translatedText, new Timestamp(System.currentTimeMillis())
        );
    }
    public void clearCache() {
        translationCache.clear();
        log.info("Translation cache cleared.");
    }

}
