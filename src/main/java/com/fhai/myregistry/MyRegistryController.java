package com.fhai.myregistry;


import com.fhai.myregistry.model.InstanceMeta;
import com.fhai.myregistry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Object findAllInstances(@RequestParam String service) {
        log.info("==> get all instances of service: {}", service);
        return myRegistryService.getAllInstances(service);
    }
}
