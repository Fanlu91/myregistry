package com.fhai.myregistry;


import com.fhai.myregistry.model.InstanceMeta;
import com.fhai.myregistry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class MyRegistryController {
    @Autowired
    RegistryService myRegistryService;

    // 注册服务
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public InstanceMeta register(@RequestParam String service, @RequestBody InstanceMeta instance) {
        log.info("==> register service: {}, instance: {}", service, instance);
        return myRegistryService.register(service, instance);
    }

    // 注销服务
    @RequestMapping(value = "/unregister", method = RequestMethod.POST)
    public InstanceMeta deregister(@RequestParam String service, @RequestBody InstanceMeta instance) {
        log.info("==> unregister service: {}, instance: {}", service, instance);
        return myRegistryService.unregister(service, instance);
    }

    /**
     * find all instances of service
     */
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public List<InstanceMeta> findAllInstances(@RequestParam String service) {
        log.info("==> get all instances of service: {}", service);
        return myRegistryService.getAllInstances(service);
    }

    @RequestMapping(value = "/renew", method = RequestMethod.POST)
    public Long renew(@RequestParam String service, @RequestBody InstanceMeta instance) {
        log.info("==> renew service: {}, instance: {}", service, instance);
        return myRegistryService.renew(instance, service);
    }

    @RequestMapping(value = "/renews", method = RequestMethod.POST)
    public Long renews(@RequestParam String services, @RequestBody InstanceMeta instance) {
        log.info("==> renew services: {}, instance: {}", services, instance);
        return myRegistryService.renew(instance, services.split(","));
    }

    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public Long version(@RequestParam String service) {
        log.info("==> get version of service: {}", service);
        return myRegistryService.version(service);
    }

    @RequestMapping(value = "/versions", method = RequestMethod.GET)
    public Map<String, Long> versions(@RequestParam String services) {
        log.info("==> get versions of services: {}", services);
        return myRegistryService.versions(services.split(","));
    }

}
