package org.example.webapplicationofthetranslationservice.exception;

public class TranslationApiException extends RuntimeException {

    public TranslationApiException(String message) {
        super(message);
    }

    public TranslationApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
