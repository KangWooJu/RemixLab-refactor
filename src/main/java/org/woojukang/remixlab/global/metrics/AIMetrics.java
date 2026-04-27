package org.woojukang.remixlab.global.metrics;

import io.micrometer.core.instrument.Timer;

public interface AIMetrics {

    Timer.Sample start();

    void stopTotal(Timer.Sample sample, String flow);

    void stopStage(Timer.Sample sample, String flow, String stage);

    void incrementRequest(String flow, String result);

    void incrementInflight(String flow);

    void decrementInflight(String flow);
}
