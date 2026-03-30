package org.woojukang.remixlab.global.config.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GptConfig {

    @Value("${openai.api.key}")
    private String apiKey;

    @Bean("openAIRestTemplate")
    public RestTemplate openAIRestTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors()
                .add(((request, body, execution) -> {
                            request.getHeaders().add("Authorization", "Bearer " + apiKey);
                            return execution.execute(request,body);
                        })
                );
        return restTemplate;

    }


}
