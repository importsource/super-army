package com.importsource.chaos.client.endpoints;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.importsource.chaos.client.configuration.AssaultException;
import com.importsource.chaos.client.configuration.AssaultExceptionConstraint;
import com.importsource.chaos.client.configuration.AssaultProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.function.Consumer;

@Data @NoArgsConstructor @Validated @JsonInclude(JsonInclude.Include.NON_NULL)
public class AssaultPropertiesUpdate {
    @Nullable
    @Min(value = 1)
    @Max(value = 10000)
    private Integer level;

    @Nullable
    @Min(value = 1)
    @Max(value = Integer.MAX_VALUE)
    private Integer latencyRangeStart;

    @Nullable
    @Min(value = 1)
    @Max(value = Integer.MAX_VALUE)
    private Integer latencyRangeEnd;

    @Nullable
    private Boolean latencyActive;

    @Nullable
    private Boolean exceptionsActive;

    @AssaultExceptionConstraint
    private AssaultException exception;

    @Nullable
    private Boolean killApplicationActive;

    @Nullable
    private volatile Boolean memoryActive;

    @Nullable
    @Min(value = 1500)
    @Max(value = Integer.MAX_VALUE)
    private Integer memoryMillisecondsHoldFilledMemory;

    @Nullable
    @Min(value = 100)
    @Max(value = 30000)
    private Integer memoryMillisecondsWaitNextIncrease;

    @Nullable
    @DecimalMax("1.0")
    @DecimalMin("0.0")
    private Double memoryFillIncrementFraction;

    @Nullable
    @DecimalMax("0.95")
    @DecimalMin("0.05")
    private Double memoryFillTargetFraction;

    @Nullable
    private String runtimeAssaultCronExpression;

    @Nullable
    private List<String> watchedCustomServices;


    private <T> void applyTo(T value, Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }

    @SuppressWarnings("all")
    public void applyTo(AssaultProperties t) {
        applyTo(level, t::setLevel);
        applyTo(latencyActive, t::setLatencyActive);
        applyTo(latencyRangeStart, t::setLatencyRangeStart);
        applyTo(latencyRangeEnd, t::setLatencyRangeEnd);

        applyTo(exceptionsActive, t::setExceptionsActive);
        applyTo(exception, t::setException);

        applyTo(killApplicationActive, t::setKillApplicationActive);

        applyTo(memoryActive, t::setMemoryActive);
        applyTo(memoryMillisecondsHoldFilledMemory, t::setMemoryMillisecondsHoldFilledMemory);
        applyTo(memoryMillisecondsWaitNextIncrease, t::setMemoryMillisecondsWaitNextIncrease);
        applyTo(memoryFillIncrementFraction, t::setMemoryFillIncrementFraction);
        applyTo(memoryFillTargetFraction, t::setMemoryFillTargetFraction);
        applyTo(runtimeAssaultCronExpression, t::setRuntimeAssaultCronExpression);
        applyTo(watchedCustomServices, t::setWatchedCustomServices);
    }
}
