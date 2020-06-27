package com.importsource.chaos.client.component;

import com.importsource.chaos.client.configuration.AssaultProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

public class ChaosMonkeyScheduler {
    private final Logger LOGGER = LoggerFactory.getLogger(ChaosMonkeyScheduler.class);

    @Nullable private final ScheduledTaskRegistrar scheduler;
    private final AssaultProperties config;
    private final ChaosMonkeyRuntimeScope runtimeScope;

    @Nullable private ScheduledTask currentTask;

    public ChaosMonkeyScheduler(ScheduledTaskRegistrar scheduler, AssaultProperties config, ChaosMonkeyRuntimeScope runtimeScope) {
        this.scheduler = scheduler;
        this.config = config;
        this.runtimeScope = runtimeScope;

        if (scheduler == null) {
            LOGGER.warn("No ScheduledTaskRegistrar available in application context, scheduler is not functional");
        }

        reloadConfig();
    }

    public void reloadConfig() {
        String cronExpression = config.getRuntimeAssaultCronExpression();
        boolean active = !"OFF".equals(cronExpression);

        if (currentTask != null) {
            LOGGER.info("Cancelling previous task");
            currentTask.cancel();
            currentTask = null;
        }

        if (active) {
            if (scheduler == null) {
                // We might consider an exception here, since the user intent could clearly not be serviced
                LOGGER.error("No scheduler available in application context, will not process schedule");
            } else {
                CronTask task = new CronTask(runtimeScope::callChaosMonkey, cronExpression);
                currentTask = scheduler.scheduleCronTask(task);
            }
        }
    }
}
