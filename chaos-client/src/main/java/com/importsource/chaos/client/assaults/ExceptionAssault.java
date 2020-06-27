package com.importsource.chaos.client.assaults;

import com.importsource.chaos.client.component.MetricEventPublisher;
import com.importsource.chaos.client.component.MetricType;
import com.importsource.chaos.client.configuration.AssaultException;
import com.importsource.chaos.client.configuration.ChaosMonkeySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hezhuofan
 */
public class ExceptionAssault implements ChaosMonkeyRequestAssault {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAssault.class);
    private final ChaosMonkeySettings settings;
    private MetricEventPublisher metricEventPublisher;

    public ExceptionAssault(ChaosMonkeySettings settings, MetricEventPublisher metricEventPublisher) {
        this.settings = settings;
        this.metricEventPublisher = metricEventPublisher;
    }

    @Override
    public boolean isActive() {
        return settings.getAssaultProperties().isExceptionsActive();
    }

    @Override
    public void attack() {
        LOGGER.info("Chaos Monkey - exception");

        AssaultException assaultException = this.settings.getAssaultProperties().getException();

        // metrics
        if (metricEventPublisher != null)
            metricEventPublisher.publishMetricEvent(MetricType.EXCEPTION_ASSAULT);

        assaultException.throwExceptionInstance();
    }
}
