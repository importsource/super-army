package com.importsource.chaos.client.assaults;

import com.importsource.chaos.client.component.MetricEventPublisher;
import com.importsource.chaos.client.component.MetricType;
import com.importsource.chaos.client.configuration.ChaosMonkeySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Thorsten Deelmann
 * @author hezhuofan
 */
public class KillAppAssault implements ChaosMonkeyRuntimeAssault, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(KillAppAssault.class);
    private ApplicationContext context;
    private final ChaosMonkeySettings settings;
    private MetricEventPublisher metricEventPublisher;

    public KillAppAssault(ChaosMonkeySettings settings, MetricEventPublisher metricEventPublisher) {
        this.settings = settings;
        this.metricEventPublisher = metricEventPublisher;
    }

    @Override
    public boolean isActive() {
        return settings.getAssaultProperties().isKillApplicationActive();
    }

    @Override
    public void attack() {
        try {
            LOGGER.info("Chaos Monkey - I am killing your Application!");

            if (metricEventPublisher != null)
                metricEventPublisher.publishMetricEvent(MetricType.KILLAPP_ASSAULT);

            int exit = SpringApplication.exit(context, (ExitCodeGenerator) () -> 0);
            Thread.sleep(5000); // wait before kill to deliver some metrics

            System.exit(exit);
        } catch (Exception e) {
            LOGGER.info("Chaos Monkey - Unable to kill the App, I am not the BOSS!");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }
}
