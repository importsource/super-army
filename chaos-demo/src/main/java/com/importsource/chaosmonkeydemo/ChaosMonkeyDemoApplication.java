package com.importsource.chaosmonkeydemo;

import com.importsource.chaos.client.annotation.EnableChaos;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableChaos
public class ChaosMonkeyDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChaosMonkeyDemoApplication.class, args);
    }

}
