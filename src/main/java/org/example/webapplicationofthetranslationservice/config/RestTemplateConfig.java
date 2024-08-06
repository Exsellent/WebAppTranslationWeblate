package org.example.webapplicationofthetranslationservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;

@Configuration
public class RestTemplateConfig {

    @Value("${weblate.api.key}")
    private String apiKey;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(
                (ClientHttpRequestInterceptor) (request, body, execution) -> {
                    request.getHeaders().set("Authorization", "Token " + apiKey);
                    return execution.execute(request, body);
                }
        );
        return restTemplate;
    }
}