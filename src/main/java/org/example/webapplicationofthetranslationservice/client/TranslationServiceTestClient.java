package org.example.webapplicationofthetranslationservice.client;

import org.springframework.web.client.RestTemplate;
import java.net.URI;

public class TranslationServiceTestClient {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        String baseUrl = "http://localhost:8080/translate";
        String inputText = "Hello world, this is my first program";

        String url = baseUrl +
                "?inputText=" + inputText.replace(" ", "%20") +
                "&sourceLang=en" +
                "&targetLang=ru" +
                "&ipAddress=127.0.0.1";

        try {
            URI uri = new URI(url);
            String response = restTemplate.getForObject(uri, String.class);
            System.out.println("Ответ: " + response);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}