package com.importsource.chaos.client.watcher;

import com.importsource.chaos.client.component.ChaosMonkeyRequestScope;
import com.importsource.chaos.client.component.MetricEventPublisher;
import com.importsource.chaos.client.component.MetricType;
import com.importsource.chaos.client.configuration.WatcherProperties;
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
public class SpringComponentAspect extends ChaosMonkeyBaseAspect {

    private final ChaosMonkeyRequestScope chaosMonkeyRequestScope;
    private MetricEventPublisher metricEventPublisher;
    private WatcherProperties watcherProperties;

    @Pointcut("within(@org.springframework.stereotype.Component *)")
    public void classAnnotatedWithComponentPointcut() {
    }

    @Pointcut("within(org.springframework.cloud.context..*)")
    public void classInSpringCloudContextPackage() {
    }


    @Around("classAnnotatedWithComponentPointcut() && !classInSpringCloudContextPackage() " +
            "&& allPublicMethodPointcut() && !classInChaosMonkeyPackage()")
    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {
        if (watcherProperties.isComponent()) {
            log.debug("Watching public method on component class: {}", pjp.getSignature());

            if (metricEventPublisher != null)
                metricEventPublisher.publishMetricEvent(calculatePointcut(pjp.toShortString()), MetricType.COMPONENT);

            MethodSignature signature = (MethodSignature) pjp.getSignature();

            chaosMonkeyRequestScope.callChaosMonkey(createSignature(signature));
        }
        return pjp.proceed();
    }
}
