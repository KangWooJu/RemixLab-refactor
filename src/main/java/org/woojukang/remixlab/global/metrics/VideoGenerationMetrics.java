package org.woojukang.remixlab.global.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class VideoGenerationMetrics implements AIMetrics{

    private final MeterRegistry meterRegistry;

    private final ConcurrentMap<String, AtomicInteger> inflightMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Counter> requestCounters = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Timer> totalLatencyTimers = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Timer> stageLatencyTimers = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        registerInflightGauge("photo_based");
        registerInflightGauge("text_based");
    }

    private void registerInflightGauge(String flow) {
        AtomicInteger inflight = new AtomicInteger(0);
        inflightMap.put(flow, inflight);

        Gauge.builder("openai.video.inflight", inflight, AtomicInteger::get)
                .description("Number of in-flight video generation requests")
                .tag("flow", flow)
                .register(meterRegistry);
    }

    @Override
    public Timer.Sample start() {
        return Timer.start(meterRegistry);
    }

    @Override
    public void stopTotal(Timer.Sample sample, String flow) {
        sample.stop(getOrCreateTotalTimer(flow));
    }

    @Override
    public void stopStage(Timer.Sample sample, String flow, String stage) {
        sample.stop(getOrCreateStageTimer(flow, stage));
    }

    @Override
    public void incrementRequest(String flow, String result) {
        getOrCreateRequestCounter(flow, result).increment();
    }

    @Override
    public void incrementInflight(String flow) {
        inflightMap.computeIfAbsent(flow, key -> {
            AtomicInteger inflight = new AtomicInteger(0);
            Gauge.builder("openai.video.inflight", inflight, AtomicInteger::get)
                    .description("Number of in-flight video generation requests")
                    .tag("flow", key)
                    .register(meterRegistry);
            return inflight;
        }).incrementAndGet();
    }

    @Override
    public void decrementInflight(String flow) {
        AtomicInteger inflight = inflightMap.get(flow);
        if (inflight != null) {
            inflight.decrementAndGet();
        }
    }

    private Counter getOrCreateRequestCounter(String flow, String result) {
        String key = flow + ":" + result;
        return requestCounters.computeIfAbsent(key, k ->
                Counter.builder("openai.video.requests")
                        .description("Number of video generation requests")
                        .tag("flow", flow)
                        .tag("result", result)
                        .register(meterRegistry)
        );
    }

    private Timer getOrCreateTotalTimer(String flow) {
        return totalLatencyTimers.computeIfAbsent(flow, k ->
                Timer.builder("openai.video.latency")
                        .description("End-to-end latency for video generation")
                        .tag("flow", flow)
                        .publishPercentileHistogram()
                        .register(meterRegistry)
        );
    }

    private Timer getOrCreateStageTimer(String flow, String stage) {
        String key = flow + ":" + stage;
        return stageLatencyTimers.computeIfAbsent(key, k ->
                Timer.builder("openai.video.stage.latency")
                        .description("Stage latency for video generation")
                        .tag("flow", flow)
                        .tag("stage", stage)
                        .publishPercentileHistogram()
                        .register(meterRegistry)
        );
    }
}
