package com.importsource.chaos.client.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "chaos.monkey")
@EqualsAndHashCode
public class ChaosMonkeyProperties {

    @Value("${enabled:false}")
    private boolean enabled;

}
