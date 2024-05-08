package com.fhai.myregistry.service;


import com.fhai.myregistry.model.InstanceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

@Slf4j
public class MyRegistryService implements RegistryService {
    // 要存每个service对应的实例
    //    Map<String, List<InstanceMeta>> map;
    // 多值map默认一个key可以存多个值，取出来就是list
    final static MultiValueMap<String, InstanceMeta> REGISTRY = new LinkedMultiValueMap<>();

    @Override
    public InstanceMeta register(String service, InstanceMeta instance) {
        // 服务名和实例名是一对多的关系
        instance.setStatus(true);
        List<InstanceMeta> instanceMetas = REGISTRY.get(service);
        if (instanceMetas != null && !instanceMetas.isEmpty()) {
            if (instanceMetas.contains(instance)) {
                log.info("instance already exist, {}", instance);
                return instance;
            }
        }
        log.info("register instance, {}", instance);
        REGISTRY.add(service, instance);

        return instance;
    }

    @Override
    public InstanceMeta unregister(String service, InstanceMeta instance) {
        instance.setStatus(false);
        List<InstanceMeta> instanceMetas = REGISTRY.get(service);
        if (instanceMetas != null && !instanceMetas.isEmpty()) {
            if (instanceMetas.contains(instance)) {
                log.info("unregister instance, {}", instance);
                instanceMetas.remove(instance);
                return instance;
            }
        }
        log.info("instance not exist, {}", instance);
        return null;
    }

    @Override
    public List<InstanceMeta> getAllInstances(String service) {
        return REGISTRY.get(service);
    }

    @Override
    public long renew(InstanceMeta instance, String... service) {
        return 0;
    }

    @Override
    public Long version(String service) {
        return null;
    }

    @Override
    public Map<String, Long> versions(String... services) {
        return null;
    }
}
