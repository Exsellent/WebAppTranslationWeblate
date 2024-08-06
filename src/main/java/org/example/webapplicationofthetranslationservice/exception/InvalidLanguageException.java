package org.example.webapplicationofthetranslationservice.exception;

public class InvalidLanguageException extends RuntimeException {
    public InvalidLanguageException(String message) {
        super(message);
    }
}
