package com.importsource.chaos.client.endpoints;

import com.importsource.chaos.client.component.ChaosMonkeyRuntimeScope;
import com.importsource.chaos.client.component.ChaosMonkeyScheduler;
import com.importsource.chaos.client.configuration.AssaultProperties;
import com.importsource.chaos.client.configuration.ChaosMonkeySettings;
import com.importsource.chaos.client.configuration.WatcherProperties;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestControllerEndpoint(enableByDefault = false, id = "chaosmonkey")
public class ChaosMonkeyRestEndpoint {

    private final ChaosMonkeySettings chaosMonkeySettings;
    private final ChaosMonkeyRuntimeScope runtimeScope;
    private final ChaosMonkeyScheduler scheduler;

    public ChaosMonkeyRestEndpoint(ChaosMonkeySettings chaosMonkeySettings, ChaosMonkeyRuntimeScope runtimeScope, ChaosMonkeyScheduler scheduler) {
        this.chaosMonkeySettings = chaosMonkeySettings;
        this.runtimeScope = runtimeScope;
        this.scheduler = scheduler;
    }

    @PostMapping("/assaults")
    public ResponseEntity<String> updateAssaultProperties(@RequestBody @Validated AssaultPropertiesUpdate assaultProperties) {
        assaultProperties.applyTo(chaosMonkeySettings.getAssaultProperties());
        scheduler.reloadConfig();

        return ResponseEntity.ok().body("Assault config has changed");
    }

    @PostMapping("/assaults/runtime/attack")
    public ResponseEntity<String> attack() {
        runtimeScope.callChaosMonkey();
        return ResponseEntity.ok("Started runtime assaults");
    }

    @GetMapping("/assaults")
    public AssaultProperties getAssaultSettings() {
        return this.chaosMonkeySettings.getAssaultProperties();
    }

    @PostMapping("/enable")
    public ResponseEntity<String> enableChaosMonkey() {
        this.chaosMonkeySettings.getChaosMonkeyProperties().setEnabled(true);
        return ResponseEntity.ok().body("Chaos Monkey is enabled");
    }

    @PostMapping("/disable")
    public ResponseEntity<String> disableChaosMonkey() {
        this.chaosMonkeySettings.getChaosMonkeyProperties().setEnabled(false);
        return ResponseEntity.ok().body("Chaos Monkey is disabled");
    }

    @GetMapping
    public ChaosMonkeySettings status() {
        return this.chaosMonkeySettings;
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        if (this.chaosMonkeySettings.getChaosMonkeyProperties().isEnabled())
            return ResponseEntity.status(HttpStatus.OK).body("Ready to be evil!");
        else
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("You switched me off!");
    }

    @PostMapping("/watchers")
    public ResponseEntity<String> updateWatcherProperties(@RequestBody @Validated WatcherPropertiesUpdate watcherProperties) {
        watcherProperties.applyTo(chaosMonkeySettings.getWatcherProperties());
        scheduler.reloadConfig();

        return ResponseEntity.ok().body("Watcher config has changed");
    }

    @GetMapping("/watchers")
    public WatcherProperties getWatcherSettings() {
        return this.chaosMonkeySettings.getWatcherProperties();
    }
}
