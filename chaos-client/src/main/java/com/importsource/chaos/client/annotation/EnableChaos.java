package com.importsource.chaos.client.annotation;

import com.importsource.chaos.client.configuration.ChaosMonkeyConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import java.lang.annotation.*;

/**
 * @author hezhuofan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ChaosMonkeyConfiguration.class)
@Profile("test")
@Documented
public @interface EnableChaos {

}