package com.importsource.chaos.client.watcher;

import com.importsource.chaos.client.component.MetricType;
import com.importsource.chaos.client.configuration.WatcherProperties;
import com.importsource.chaos.client.component.ChaosMonkeyRequestScope;
import com.importsource.chaos.client.component.MetricEventPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * @author Benjamin Wilms
 */

@Aspect
@AllArgsConstructor
@Slf4j
public class SpringControllerAspect extends ChaosMonkeyBaseAspect {

    private final ChaosMonkeyRequestScope chaosMonkeyRequestScope;
    private MetricEventPublisher metricEventPublisher;
    private WatcherProperties watcherProperties;

    @Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void classAnnotatedWithControllerPointcut() {
    }

    @Around("classAnnotatedWithControllerPointcut() && allPublicMethodPointcut() && !classInChaosMonkeyPackage()")
    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {

        if (watcherProperties.isController()) {
            log.debug("Watching public method on controller class: {}", pjp.getSignature());

            if (metricEventPublisher != null)
                metricEventPublisher.publishMetricEvent(calculatePointcut(pjp.toShortString()), MetricType.CONTROLLER);

            MethodSignature signature = (MethodSignature) pjp.getSignature();

            chaosMonkeyRequestScope.callChaosMonkey(createSignature(signature));
        }
        return pjp.proceed();
    }

}
