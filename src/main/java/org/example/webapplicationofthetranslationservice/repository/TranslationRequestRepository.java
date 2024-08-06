package org.example.webapplicationofthetranslationservice.repository;

import org.example.webapplicationofthetranslationservice.model.TranslationRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class TranslationRequestRepository {

    private final JdbcTemplate jdbcTemplate;

    public TranslationRequestRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void save(TranslationRequest request) {
        jdbcTemplate.update(
                "INSERT INTO translation_request (ip_address, input_text, translated_text) VALUES (?, ?, ?)",
                request.getIpAddress(), request.getInputText(), request.getTranslatedText()
        );
    }


}
