package com.flhai.myregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({MyRegistryConfigProperties.class})
public class MyregistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyregistryApplication.class, args);
    }

}
