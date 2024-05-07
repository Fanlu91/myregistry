package com.fhai.myregistry.service;

import com.flhai.iregistry.model.InstanceMeta;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class MyRegistryService implements RegistryService{
    @Override
    public InstanceMeta register(String service, InstanceMeta instance) {
        return null;
    }

    @Override
    public InstanceMeta unregister(String service, InstanceMeta instance) {
        return null;
    }

    @Override
    public List<InstanceMeta> getAllInstances(String service) {
        return null;
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
