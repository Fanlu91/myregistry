package com.flhai.myregistry.service;


import com.flhai.myregistry.model.InstanceMeta;

import java.util.List;
import java.util.Map;

public interface RegistryService {
    // 最基础的三个方法
    InstanceMeta register(String service, InstanceMeta instance);

    InstanceMeta unregister(String service, InstanceMeta instance);

    List<InstanceMeta> getAllInstances(String service);

    // todo 添加一些高级功能
    long renew(InstanceMeta instance, String... service);

    long version(String service);

    Map<String, Long> versions(String... services);
}
