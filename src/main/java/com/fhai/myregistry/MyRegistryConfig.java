package com.fhai.myregistry;

import com.fhai.myregistry.service.MyRegistryService;
import com.fhai.myregistry.service.RegistryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRegistryConfig {
    @Bean
    public MyRegistryConfigProperties myRegistryConfigProperties() {
        return new MyRegistryConfigProperties();
    }

    @Bean
    public RegistryService myRegistryService() {
        return new MyRegistryService();
    }
}
