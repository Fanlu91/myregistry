package com.flhai.myregistry.service;


import com.flhai.myregistry.cluster.Snapshot;
import com.flhai.myregistry.model.InstanceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class MyRegistryService implements RegistryService {
    // 要存每个service对应的实例
    //    Map<String, List<InstanceMeta>> map;
    // 多值map默认一个key可以存多个值，取出来就是list
    final static MultiValueMap<String, InstanceMeta> REGISTRY = new LinkedMultiValueMap<>();

    // 版本
    final static Map<String, Long> VERSIONS = new ConcurrentHashMap<>();
    public final static Map<String, Long> TIMESTAMP = new ConcurrentHashMap<>();
    public static AtomicLong VERSION = new AtomicLong(0);

    @Override
    public synchronized InstanceMeta register(String service, InstanceMeta instance) {
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
        renew(instance, service);
        VERSIONS.put(service, VERSION.incrementAndGet());
        REGISTRY.add(service, instance);
        return instance;
    }

    @Override
    public synchronized InstanceMeta unregister(String service, InstanceMeta instance) {
        instance.setStatus(false);
        List<InstanceMeta> instanceMetas = REGISTRY.get(service);
        if (instanceMetas == null || instanceMetas.isEmpty()) {
            log.info("instance not exist, {}", instance);
            return null;
        }
        log.info("unregister instance, {}", instance);
        instanceMetas.remove(instance);
        instance.setStatus(false);
        renew(instance, service);
        VERSIONS.put(service, VERSION.incrementAndGet());
        return instance;

    }

    @Override
    public List<InstanceMeta> getAllInstances(String service) {
        return REGISTRY.get(service);
    }


    @Override
    public synchronized long renew(InstanceMeta instance, String... services) {
        long now = System.currentTimeMillis();
        for (String s : services) {
            TIMESTAMP.put(s + "@" + instance.toUrl(), now);
        }
        return now;
    }

    @Override
    public long version(String service) {
        return VERSIONS.get(service);
    }

    @Override
    public Map<String, Long> versions(String... services) {
        Map<String, Long> versions = new HashMap<>();
        for (String service : services) {
            versions.put(service, VERSIONS.get(service));
        }
        return versions;
    }

    /**
     * deep copy
     *
     * @return
     */
    public static synchronized Snapshot snapshot() {
        LinkedMultiValueMap<String, InstanceMeta> registry = new LinkedMultiValueMap<>();
        registry.addAll(REGISTRY);
        Map<String, Long> versions = new HashMap<>(VERSIONS);
        Map<String, Long> timestamp = new HashMap<>(TIMESTAMP);
        return new Snapshot(registry, versions, timestamp, VERSION.get());
    }


    public static synchronized long restore(Snapshot snapshot) {
        REGISTRY.clear();
        REGISTRY.addAll(snapshot.getREGISTRY());
        VERSIONS.clear();
        VERSIONS.putAll(snapshot.getVERSIONS());
        TIMESTAMP.clear();
        TIMESTAMP.putAll(snapshot.getTIMESTAMP());
        VERSION.set(snapshot.getVERSION());
        return VERSION.get();
    }
}
