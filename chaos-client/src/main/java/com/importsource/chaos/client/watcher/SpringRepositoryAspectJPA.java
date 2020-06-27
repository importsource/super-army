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
public class SpringRepositoryAspectJPA extends ChaosMonkeyBaseAspect {

    private final ChaosMonkeyRequestScope chaosMonkeyRequestScope;
    private MetricEventPublisher metricEventPublisher;
    private WatcherProperties watcherProperties;

    @Pointcut("this(org.springframework.data.repository.Repository) || within(@org.springframework.data.repository.RepositoryDefinition *)")
    public void implementsCrudRepository() {
    }

    @Around("implementsCrudRepository() && allPublicMethodPointcut() && !classInChaosMonkeyPackage()")
    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {

        if (watcherProperties.isRepository()) {
            log.debug("Watching public method on repository class: {}", pjp.getSignature());

            if (metricEventPublisher != null)
                metricEventPublisher.publishMetricEvent(calculatePointcut(pjp.toShortString()), MetricType.REPOSITORY);

            MethodSignature signature = (MethodSignature) pjp.getSignature();

            chaosMonkeyRequestScope.callChaosMonkey(createSignature(signature));
        }
        return pjp.proceed();
    }

}
