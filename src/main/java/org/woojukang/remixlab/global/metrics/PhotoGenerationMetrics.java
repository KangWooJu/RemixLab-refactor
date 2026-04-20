package org.woojukang.remixlab.global.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PhotoGenerationMetrics implements AIMetrics{

    private final MeterRegistry meterRegistry;
    private Counter successCounter;
    private Counter failCounter;

    @PostConstruct
    public void init() {
        this.successCounter = Counter.builder("openai.image.success.count")
                .description("Number of successful image generations")
                .register(meterRegistry);

        this.failCounter = Counter.builder("openai.image.fail.count")
                .description("Number of failed image generations")
                .register(meterRegistry);
    }

    @Override
    public Timer.Sample start() {
        return Timer.start(meterRegistry);
    }

    @Override
    public void stop(Timer.Sample sample) {
        sample.stop(Timer.builder("openai.image.latency")
                .description("End-to-end latency for OpenAI image generation")
                .register(meterRegistry));
    }

    public void success() {
        successCounter.increment();
    }

    public void fail() {
        failCounter.increment();
    }


}
