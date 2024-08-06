CREATE TABLE IF NOT EXISTS translation_requests (
                                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                                    ip_address VARCHAR(45) NOT NULL,
    input_text TEXT NOT NULL,
    translated_text TEXT NOT NULL,
    request_time TIMESTAMP NOT NULL
    );