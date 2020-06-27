package com.importsource.chaos.client.assaults;

import com.importsource.chaos.client.component.MetricEventPublisher;
import com.importsource.chaos.client.component.MetricType;
import com.importsource.chaos.client.configuration.ChaosMonkeySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Thorsten Deelmann
 * @author hezhuofan
 */
public class LatencyAssault implements ChaosMonkeyRequestAssault {

    private static final Logger LOGGER = LoggerFactory.getLogger(LatencyAssault.class);

    private final ChaosMonkeySettings settings;
    private final ChaosMonkeyLatencyAssaultExecutor assaultExecutor;

    private MetricEventPublisher metricEventPublisher;

    private AtomicInteger atomicTimeoutGauge;

    public LatencyAssault(ChaosMonkeySettings settings, MetricEventPublisher metricEventPublisher, ChaosMonkeyLatencyAssaultExecutor executor) {
        this.settings = settings;
        this.metricEventPublisher = metricEventPublisher;
        this.atomicTimeoutGauge = new AtomicInteger(0);
        this.assaultExecutor = executor;
    }

    public LatencyAssault(ChaosMonkeySettings settings, MetricEventPublisher metricEventPublisher){
        this(settings, metricEventPublisher, new LatencyAssaultExecutor());
    }

    @Override
    public boolean isActive() {
        return settings.getAssaultProperties().isLatencyActive();
    }

    @Override
    public void attack() {
        LOGGER.debug("Chaos Monkey - timeout");

        atomicTimeoutGauge.set(determineLatency());

        // metrics
        if (metricEventPublisher != null) {
            metricEventPublisher.publishMetricEvent(MetricType.LATENCY_ASSAULT);
            metricEventPublisher.publishMetricEvent(MetricType.LATENCY_ASSAULT, atomicTimeoutGauge);
        }

        assaultExecutor.execute(atomicTimeoutGauge.get());
    }

    private int determineLatency() {
        final int latencyRangeStart =
                settings.getAssaultProperties().getLatencyRangeStart();
        final int latencyRangeEnd =
                settings.getAssaultProperties().getLatencyRangeEnd();

        if (latencyRangeStart == latencyRangeEnd) {
            return latencyRangeStart;
        } else {
            return ThreadLocalRandom.current().nextInt(latencyRangeStart,
                    latencyRangeEnd);
        }
    }
}
