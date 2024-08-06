package org.example.webapplicationofthetranslationservice;

import org.example.webapplicationofthetranslationservice.exception.InvalidLanguageException;
import org.example.webapplicationofthetranslationservice.service.TranslationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TranslationServiceImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TranslationServiceImpl translationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testTranslateText() {
        String ipAddress = "127.0.0.1";
        String inputText = "Hello";
        String sourceLang = "en";
        String targetLang = "es";
        String expectedTranslation = "Hola";

        // Мокируем вызов перевода
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(expectedTranslation);

        String actualTranslation = translationService.translateText(ipAddress, inputText, sourceLang, targetLang);

        assertThat(actualTranslation).isEqualTo(expectedTranslation);

        // Проверяем, что был вызван метод перевода
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));

        // Проверяем, что результат был сохранен в базу данных
        verify(jdbcTemplate, times(1)).update(
                eq("INSERT INTO translation_requests (ip_address, input_text, translated_text, request_time) VALUES (?, ?, ?, ?)"),
                eq(ipAddress),
                eq(inputText),
                eq(expectedTranslation),
                any(Timestamp.class)
        );
    }

    @Test
    public void testTranslateTextWithInvalidLanguage() {
        String ipAddress = "127.0.0.1";
        String inputText = "Hello";
        String sourceLang = "invalid_lang";
        String targetLang = "es";

        // Проверяем выброс исключения при недопустимом языке
        assertThatThrownBy(() -> translationService.translateText(ipAddress, inputText, sourceLang, targetLang))
                .isInstanceOf(InvalidLanguageException.class)
                .hasMessage("Unsupported language specified");
    }
}
