package com.importsource.chaos.client.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Benjamin Wilms
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "chaos.monkey.watcher")
@EqualsAndHashCode
public class WatcherProperties {

    @Value("${controller:false}")
    private boolean controller;

    @Value("${restController:false}")
    private boolean restController;

    @Value("${service:true}")
    private boolean service;

    @Value("${repository:false}")
    private boolean repository;

    @Value("${component:false}")
    private boolean component;
}
