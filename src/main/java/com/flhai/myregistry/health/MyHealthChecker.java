package com.flhai.myregistry.health;

import com.flhai.myregistry.model.InstanceMeta;
import com.flhai.myregistry.service.MyRegistryService;
import com.flhai.myregistry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
public class MyHealthChecker implements HealthChecker {

    final static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    long timeout = 20 * 1000;

    RegistryService registryService;

    public MyHealthChecker(RegistryService registryService) {
        this.registryService = registryService;
    }

    @Override
    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("health checker running...");
            long now = System.currentTimeMillis();
            MyRegistryService.TIMESTAMP.keySet().forEach(serviceAndInstance -> {
                long last = MyRegistryService.TIMESTAMP.get(serviceAndInstance);
                if (now - last > timeout) {
                    log.info("serviceAndInstance {} is down", serviceAndInstance);
                    int i = serviceAndInstance.indexOf("@");
                    String service = serviceAndInstance.substring(0, i);
                    String instanceUrl = serviceAndInstance.substring(i + 1);
                    InstanceMeta instanceMeta = InstanceMeta.from(instanceUrl);
                    registryService.unregister(service, instanceMeta);
                    MyRegistryService.TIMESTAMP.remove(serviceAndInstance);
                }
            });
        }, 10000, 10, java.util.concurrent.TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        scheduledExecutorService.shutdown();
    }
}
