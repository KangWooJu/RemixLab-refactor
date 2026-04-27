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
public class PhotoGenerationMetrics implements AIMetrics{

    private final MeterRegistry meterRegistry;

    private final ConcurrentMap<String, AtomicInteger> inflightMap = new ConcurrentHashMap<>(); // flow 별 현재 처리중 요청 수 저장
    private final ConcurrentMap<String, Counter> requestCounters = new ConcurrentHashMap<>(); // 요청 수 카운터 저장
    private final ConcurrentMap<String, Timer> totalLatencyTimers = new ConcurrentHashMap<>(); // flow별 전체 latency timer 저장
    private final ConcurrentMap<String, Timer> stageLatencyTimers = new ConcurrentHashMap<>(); // flow + stage별 latency timer 저장

    @PostConstruct
    public void init() {
        registerInflightGauge("plot");
        registerInflightGauge("direct");
    }

    private void registerInflightGauge(String flow) {
        AtomicInteger inflight = new AtomicInteger(0);
        inflightMap.put(flow, inflight);

        Gauge.builder("openai.image.inflight", inflight, AtomicInteger::get)
                .description("Number of in-flight image generation requests")
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
        // 해당 flow의 진행 중 요청 수 증가
        inflightMap.computeIfAbsent(flow, key -> {
            AtomicInteger inflight = new AtomicInteger(0);
            Gauge.builder("openai.image.inflight", inflight, AtomicInteger::get)
                    .description("Number of in-flight image generation requests")
                    .tag("flow", key)
                    .register(meterRegistry);
            return inflight;
        }).incrementAndGet();
    }

    @Override
    public void decrementInflight(String flow) {
        // 해당 flow의 진행 중 요청 수 감소
        AtomicInteger inflight = inflightMap.get(flow);
        if (inflight != null) {
            inflight.decrementAndGet();
        }
    }

    private Counter getOrCreateRequestCounter(String flow, String result) {
        // flow/result 조합별 counter를 재사용하기 위한 key
        String key = flow + ":" + result;
        return requestCounters.computeIfAbsent(key, k ->
                Counter.builder("openai.image.requests")
                        .description("Number of image generation requests")
                        .tag("flow", flow)
                        .tag("result", result)
                        .register(meterRegistry)
        );
    }

    private Timer getOrCreateTotalTimer(String flow) {
        // flow별 total latency timer를 재사용
        return totalLatencyTimers.computeIfAbsent(flow, k ->
                Timer.builder("openai.image.latency")
                        .description("End-to-end latency for image generation")
                        .tag("flow", flow)
                        .publishPercentileHistogram()
                        .register(meterRegistry)
        );
    }

    private Timer getOrCreateStageTimer(String flow, String stage) {
        // flow별 total latency timer 재사용
        String key = flow + ":" + stage;
        return stageLatencyTimers.computeIfAbsent(key, k ->
                Timer.builder("openai.image.stage.latency")
                        .description("Stage latency for image generation")
                        .tag("flow", flow)
                        .tag("stage", stage)
                        .publishPercentileHistogram()
                        .register(meterRegistry)
        );
    }

}
