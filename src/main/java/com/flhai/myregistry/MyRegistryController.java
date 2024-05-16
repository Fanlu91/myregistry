package com.flhai.myregistry;


import com.alibaba.fastjson.JSON;
import com.flhai.myregistry.cluster.Cluster;
import com.flhai.myregistry.cluster.Server;
import com.flhai.myregistry.cluster.Snapshot;
import com.flhai.myregistry.model.InstanceMeta;
import com.flhai.myregistry.service.MyRegistryService;
import com.flhai.myregistry.service.RegistryService;
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

    @Autowired
    Cluster cluster;

    // 注册服务
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public InstanceMeta register(@RequestParam String service, @RequestBody String instance) {

        log.info("==> register service: {}", service);
        log.info("==> instance: {}", instance);
        checkLeader();
        InstanceMeta instanceMeta = JSON.parseObject(instance, InstanceMeta.class);
        return myRegistryService.register(service, instanceMeta);
    }

    private void checkLeader() {
        if (!cluster.self().isLeader()) {
            throw new RuntimeException("current node is not leader, the leader is " + cluster.leader().getUrl());
        }
    }

    // 注销服务
    @RequestMapping(value = "/unregister", method = RequestMethod.POST)
    public InstanceMeta unregister(@RequestParam String service, @RequestBody InstanceMeta instance) {
        checkLeader();
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
    public long renew(@RequestParam String service, @RequestBody InstanceMeta instance) {
        log.info("==> renew service: {}, instance: {}", service, instance);
        return myRegistryService.renew(instance, service);
    }

    @RequestMapping(value = "/renews", method = RequestMethod.POST)
    public long renews(@RequestParam String services, @RequestBody InstanceMeta instance) {
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

    // info
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public Server info() {
        log.info("==> get self info: {}", cluster.self());
        return cluster.self();
    }

    // cluster
    @RequestMapping(value = "/cluster", method = RequestMethod.GET)
    public List<Server> cluster() {
        log.info("==> get cluster info: {}", cluster.getServers());
        return cluster.getServers();
    }

    // leader
    @RequestMapping(value = "/leader", method = RequestMethod.GET)
    public Server leader() {
        log.info("==> get leader info: {}", cluster.leader());
        return cluster.leader();
    }

    // snapshot
    @RequestMapping(value = "/snapshot", method = RequestMethod.GET)
    public Snapshot snapshot() {
        log.info("==> get snapshot info: {}", MyRegistryService.snapshot());
        return MyRegistryService.snapshot();
    }

}
