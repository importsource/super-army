package com.importsource.chaos.client.endpoints;

import com.importsource.chaos.client.configuration.AssaultProperties;
import com.importsource.chaos.client.configuration.ChaosMonkeySettings;
import com.importsource.chaos.client.configuration.WatcherProperties;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.jmx.annotation.JmxEndpoint;

/**
 * @author Benjamin Wilms
 */
@JmxEndpoint(enableByDefault = false, id = "chaosmonkeyjmx")
public class ChaosMonkeyJmxEndpoint {

    private final ChaosMonkeySettings chaosMonkeySettings;

    public ChaosMonkeyJmxEndpoint(ChaosMonkeySettings chaosMonkeySettings) {
        this.chaosMonkeySettings = chaosMonkeySettings;
    }

    @ReadOperation
    public AssaultProperties getAssaultProperties() {
        return chaosMonkeySettings.getAssaultProperties();
    }

    @WriteOperation
    public String toggleLatencyAssault() {
        this.chaosMonkeySettings.getAssaultProperties().setLatencyActive(!this.getAssaultProperties().isLatencyActive());
        return String.valueOf(this.getAssaultProperties().isLatencyActive());
    }

    @WriteOperation
    public String toggleExceptionAssault() {
        this.chaosMonkeySettings.getAssaultProperties().setExceptionsActive(!this.getAssaultProperties().isExceptionsActive());
        return String.valueOf(this.getAssaultProperties().isExceptionsActive());
    }

    @WriteOperation
    public String toggleKillApplicationAssault() {
        this.chaosMonkeySettings.getAssaultProperties().setKillApplicationActive(!this.getAssaultProperties().isKillApplicationActive());
        return String.valueOf(this.getAssaultProperties().isKillApplicationActive());
    }

    @ReadOperation()
    public String isChaosMonkeyActive() {
        return String.valueOf(this.chaosMonkeySettings.getChaosMonkeyProperties().isEnabled());
    }

    @WriteOperation
    public String enableChaosMonkey() {
        this.chaosMonkeySettings.getChaosMonkeyProperties().setEnabled(true);
        return "Chaos Monkey is enabled";
    }

    @WriteOperation
    public String disableChaosMonkey() {
        this.chaosMonkeySettings.getChaosMonkeyProperties().setEnabled(false);
        return "Chaos Monkey is disabled";
    }

    @ReadOperation
    public WatcherProperties getWatcherProperties() {
        return this.chaosMonkeySettings.getWatcherProperties();
    }


}
