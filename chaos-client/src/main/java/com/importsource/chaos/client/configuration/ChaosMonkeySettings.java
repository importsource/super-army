package com.importsource.chaos.client.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author Benjamin Wilms
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
public class ChaosMonkeySettings {

    @NotNull
    private ChaosMonkeyProperties chaosMonkeyProperties;
    @NotNull
    private AssaultProperties assaultProperties;
    @NotNull
    private WatcherProperties watcherProperties;

}
