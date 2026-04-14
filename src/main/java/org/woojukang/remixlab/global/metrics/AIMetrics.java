package org.woojukang.remixlab.global.metrics;

import io.micrometer.core.instrument.Timer;

public interface AIMetrics {

    Timer.Sample start();

    void stop(Timer.Sample sample);
}
