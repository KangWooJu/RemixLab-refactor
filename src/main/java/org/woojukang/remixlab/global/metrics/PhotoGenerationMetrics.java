package org.woojukang.remixlab.global.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PhotoGenerationMetrics implements AIMetrics{

    private final MeterRegistry meterRegistry;

    @Override
    public Timer.Sample start() {
        return Timer.start(meterRegistry);
    }

    @Override
    public void stop(Timer.Sample sample) {
        sample.stop(Timer.builder("openai.image.total")
                .description("End-to-end latency for OpenAI image generation")
                .register(meterRegistry));
    }
}
