package com.flhai.myregistry.cluster;

import com.flhai.myregistry.MyRegistryConfigProperties;
import com.flhai.myregistry.http.HttpInvoker;
import com.flhai.myregistry.service.MyRegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class Cluster {
    MyRegistryConfigProperties myRegistryConfigProperties;

    @Value("${server.port}")
    String port;

    String host;

    Server MYSELF;

    public Cluster(MyRegistryConfigProperties myRegistryConfigProperties) {
        this.myRegistryConfigProperties = myRegistryConfigProperties;
    }

    public List<Server> serverList;

    public void init() {

        host = new InetUtils(new InetUtilsProperties()).findFirstNonLoopbackHostInfo().getIpAddress();
        log.debug("==> findFirstNonLoopbackHostInfo: {}", host);

        Server self = new Server("http://" + host + ":" + port, true, false, -1L);
        MYSELF = self;
        initServers();
    }

    private void initServers() {
        this.serverList = new ArrayList<>();
        myRegistryConfigProperties.getServerList().forEach(url -> {
            Server server = new Server();
            if (url.contains("localhost")) {
                url = url.replace("localhost", host);
            } else if (url.contains("127.0.0.1")) {
                url = url.replace("127.0.0.1", host);
            }
            // if server is myself, then add MYSELF to serverList
            // when update later on, MYSELF will be updated
            if (url.equals(MYSELF.getUrl())) {
                serverList.add(MYSELF);
            } else {
                server.setUrl(url);
                server.setStatus(false);
                server.setLeader(false);
                server.setVersion(-1L);
                serverList.add(server);
            }
        });
    }

    public Server self() {
        MYSELF.setVersion(MyRegistryService.VERSION.get());
        return MYSELF;
    }

    public Server leader() {
        return serverList.stream().filter(Server::isStatus).filter(Server::isLeader).findFirst().orElse(null);
    }

    public List<Server> getServers() {
        return serverList;
    }
}
