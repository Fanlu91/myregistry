package com.flhai.myregistry;

import com.flhai.myregistry.cluster.Cluster;
import com.flhai.myregistry.health.HealthChecker;
import com.flhai.myregistry.health.MyHealthChecker;
import com.flhai.myregistry.service.MyRegistryService;
import com.flhai.myregistry.service.RegistryService;
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
