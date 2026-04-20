package org.woojukang.remixlab.global.config.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GptConfig {

    @Value("${openai.api.key}")
    private String apiKey;

    @Bean("openAIWebClient")
    public WebClient openAIWebClient() {
        return WebClient.builder()
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .codecs(configurer ->
                        configurer.defaultCodecs()
                                .maxInMemorySize(10 * 1024 * 1024) // 10MB
                )
                .build();
    }

}
