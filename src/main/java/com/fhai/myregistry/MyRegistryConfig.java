package com.fhai.myregistry;

import com.fhai.myregistry.cluster.Cluster;
import com.fhai.myregistry.health.HealthChecker;
import com.fhai.myregistry.health.MyHealthChecker;
import com.fhai.myregistry.service.MyRegistryService;
import com.fhai.myregistry.service.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRegistryConfig {

    @Bean
    public RegistryService myRegistryService() {
        return new MyRegistryService();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public HealthChecker myHealthChecker(@Autowired RegistryService registryService) {
        return new MyHealthChecker(registryService);
    }

    @Bean
    public MyRegistryConfigProperties myRegistryConfigProperties() {
        return new MyRegistryConfigProperties();
    }

    @Bean(initMethod = "init")
    public Cluster cluster(@Autowired MyRegistryConfigProperties myRegistryConfigProperties) {
        return new Cluster(myRegistryConfigProperties);
    }

}
