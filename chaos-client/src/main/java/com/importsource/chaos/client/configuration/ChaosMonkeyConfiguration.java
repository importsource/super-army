package com.importsource.chaos.client.configuration;

import com.importsource.chaos.client.assaults.*;
import com.importsource.chaos.client.component.*;
import com.importsource.chaos.client.endpoints.ChaosMonkeyJmxEndpoint;
import com.importsource.chaos.client.endpoints.ChaosMonkeyRestEndpoint;
import com.importsource.chaos.client.watcher.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author Benjamin Wilms
 * @author hezhuofan
 */
@Configuration
@EnableConfigurationProperties({ChaosMonkeyProperties.class, AssaultProperties.class, WatcherProperties.class})
@EnableScheduling
public class ChaosMonkeyConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChaosMonkeyConfiguration.class);
    private final ChaosMonkeyProperties chaosMonkeyProperties;
    private final WatcherProperties watcherProperties;
    private final AssaultProperties assaultProperties;

    public ChaosMonkeyConfiguration(ChaosMonkeyProperties chaosMonkeyProperties, WatcherProperties watcherProperties,
                                    AssaultProperties assaultProperties) {
        this.chaosMonkeyProperties = chaosMonkeyProperties;
        this.watcherProperties = watcherProperties;
        this.assaultProperties = assaultProperties;

        try {
            String chaosLogo = StreamUtils.copyToString(new ClassPathResource("chaos-logo.txt").getInputStream(), Charset.defaultCharset());
            LOGGER.info(chaosLogo);
        } catch (IOException e) {
            LOGGER.info("Chaos Monkey - ready to do evil");
        }

    }

    @Bean
    @ConditionalOnClass(name = "io.micrometer.core.instrument.MeterRegistry")
    public Metrics metrics() {
        return new Metrics();
    }


    @Bean
    public MetricEventPublisher publisher() {
        return new MetricEventPublisher();
    }

    @Bean
    public ChaosMonkeySettings settings() {
        return new ChaosMonkeySettings(chaosMonkeyProperties, assaultProperties, watcherProperties);
    }

    @Bean
    public LatencyAssault latencyAssault() {
        return new LatencyAssault(settings(), publisher());
    }

    @Bean
    public ExceptionAssault exceptionAssault() {
        return new ExceptionAssault(settings(), publisher());
    }

    @Bean
    public KillAppAssault killAppAssault() {
        return new KillAppAssault(settings(), publisher());
    }

    @Bean
    public MemoryAssault memoryAssault() {
        return new MemoryAssault(Runtime.getRuntime(), settings(), publisher());
    }

    @Bean
    public ChaosMonkeyRequestScope chaosMonkeyRequestScope(List<ChaosMonkeyRequestAssault> chaosMonkeyAssaults,
                                                           List<ChaosMonkeyAssault> allAssaults) {
        return new ChaosMonkeyRequestScope(settings(), chaosMonkeyAssaults, allAssaults, publisher());
    }

    @Bean
    public ChaosMonkeyScheduler scheduler(@Nullable TaskScheduler scheduler, ChaosMonkeyRuntimeScope runtimeScope) {
        ScheduledTaskRegistrar registrar = null;
        if (scheduler != null) {
            registrar = new ScheduledTaskRegistrar();
            registrar.setTaskScheduler(scheduler);
        }
        return new ChaosMonkeyScheduler(registrar, assaultProperties, runtimeScope);
    }

    @Bean
    public ChaosMonkeyRuntimeScope chaosMonkeyRuntimeScope(List<ChaosMonkeyRuntimeAssault> chaosMonkeyAssaults) {
        return new ChaosMonkeyRuntimeScope(settings(), chaosMonkeyAssaults);
    }

    @Bean
    @DependsOn("chaosMonkeyRequestScope")
    public SpringControllerAspect controllerAspect(ChaosMonkeyRequestScope chaosMonkeyRequestScope) {
        return new SpringControllerAspect(chaosMonkeyRequestScope, publisher(), watcherProperties);
    }

    @Bean
    @DependsOn("chaosMonkeyRequestScope")
    public SpringRestControllerAspect restControllerAspect(ChaosMonkeyRequestScope chaosMonkeyRequestScope) {
        return new SpringRestControllerAspect(chaosMonkeyRequestScope, publisher(), watcherProperties);
    }

    @Bean
    @DependsOn("chaosMonkeyRequestScope")
    public SpringServiceAspect serviceAspect(ChaosMonkeyRequestScope chaosMonkeyRequestScope) {
        return new SpringServiceAspect(chaosMonkeyRequestScope, publisher(), watcherProperties);
    }

    @Bean
    @DependsOn("chaosMonkeyRequestScope")
    public SpringComponentAspect componentAspect(ChaosMonkeyRequestScope chaosMonkeyRequestScope) {
        return new SpringComponentAspect(chaosMonkeyRequestScope, publisher(), watcherProperties);
    }

    @Bean
    @DependsOn("chaosMonkeyRequestScope")
    @ConditionalOnClass(name = "org.springframework.data.repository.Repository")
    // Creates aspects that match interfaces annotated with @Repository
    public SpringRepositoryAspectJPA repositoryAspectJPA(ChaosMonkeyRequestScope chaosMonkeyRequestScope) {
        return new SpringRepositoryAspectJPA(chaosMonkeyRequestScope, publisher(), watcherProperties);
    }

    @Bean
    @DependsOn("chaosMonkeyRequestScope")
    // creates aspects that match simple classes annotated with @repository
    public SpringRepositoryAspectJDBC repositoryAspectJDBC(ChaosMonkeyRequestScope chaosMonkeyRequestScope) {
        return new SpringRepositoryAspectJDBC(chaosMonkeyRequestScope, publisher(), watcherProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public ChaosMonkeyRestEndpoint chaosMonkeyRestEndpoint(ChaosMonkeyRuntimeScope runtimeScope, ChaosMonkeyScheduler scheduler) {
        return new ChaosMonkeyRestEndpoint(settings(), runtimeScope, scheduler);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public ChaosMonkeyJmxEndpoint chaosMonkeyJmxEndpoint() {
        return new ChaosMonkeyJmxEndpoint(settings());
    }
}
