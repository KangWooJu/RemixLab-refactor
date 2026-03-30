package org.woojukang.remixlab.global.config.app;

import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;


import java.time.Duration;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "dalleExecutor")
    public Executor asyncExecutor(ThreadPoolTaskExecutorBuilder builder){
        return builder
                .corePoolSize(20)
                .maxPoolSize(100)
                .queueCapacity(500)
                .keepAlive(Duration.ofSeconds(60))
                .threadNamePrefix("dalleExceutor")
                .build();
    }
}
