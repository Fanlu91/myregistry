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

    private List<Server> serverList;

    public void init() {

        host = new InetUtils(new InetUtilsProperties()).findFirstNonLoopbackHostInfo().getIpAddress();
        log.debug("==> findFirstNonLoopbackHostInfo: {}", host);

        Server self = new Server("http://" + host + ":" + port, true, false, -1L);
        MYSELF = self;

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
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("cluster check running...");
            try {
                updateServers();
                electLeader();
                syncSnapshotFromLeader();

            } catch (Exception e) {
                log.error("cluster check error", e);
            }

        }, 1, 5, TimeUnit.SECONDS);
    }

    private void syncSnapshotFromLeader() {
        if (!MYSELF.isLeader() && MYSELF.getVersion() < leader().getVersion()) {
            log.info("sync snapshot from leader: {}", leader());
            Snapshot snapshot = HttpInvoker.httpGet(leader().getUrl() + "/snapshot", Snapshot.class);
            log.info("restore snapshot: {}", snapshot);
            MyRegistryService.restore(snapshot);
        }
    }

    /**
     * elect leader
     */
    private void electLeader() {
        List<Server> leaders = this.serverList.stream().filter(Server::isStatus).filter(Server::isLeader).collect(Collectors.toList());
        if (leaders.isEmpty()) {
            log.info("elect for no leader");
            elect();
        } else if (leaders.size() > 1) {
            log.info("elect for multiple leaders");
            elect();
        } else {
            log.info("no need to elect, leader is {}", leaders.get(0));
        }
    }

    /**
     * elect 的方法
     * 1. 各个节点自己选，算法保证大家选的是同一个
     * 2. 外部有一个分布式锁，保证只有一个节点能够选举成功
     * 3. 分布式一致性算法，raft, paxos, zab等
     * <p>
     * 这里采用1
     */
    private void elect() {
        Server candidate = null;
        for (Server server : serverList) {
            server.setLeader(false);
            if (server.isStatus()) {
                if (candidate == null) {
                    candidate = server;
                } else {
                    if (server.getUrl().compareTo(candidate.getUrl()) < 0) {
                        candidate = server;
                    }
                }
            }
        }
        if (candidate != null) {
            candidate.setLeader(true);
            log.info("new leader is {}", candidate);
        } else {
            log.info("elect failed. no server available.");
        }
//        MYSELF.setLeader(candidate != null && candidate.equals(MYSELF));
    }

    /**
     * get and update all available servers
     */
    private void updateServers() {
        serverList.stream().parallel().forEach(server -> {
            try {
                if (server.equals(MYSELF)) {
                    return;
                }
                Server info = HttpInvoker.httpGet(server.getUrl() + "/info", Server.class);
                // if info exits, then the server is available
                if (info != null) {
                    server.setStatus(true);
                    server.setLeader(info.isLeader());
                    server.setVersion(info.getVersion());
                } else {
                    server.setStatus(false);
                }
            } catch (Exception e) {
                log.error("update server error " + server.getUrl());
//                e.printStackTrace();
                server.setStatus(false);
                server.setLeader(false);
            }
        });

    }

    public Server self() {
        return MYSELF;
    }

    public Server leader() {
        return serverList.stream().filter(Server::isStatus).filter(Server::isLeader).findFirst().orElse(null);
    }

    public List<Server> getServers() {
        return serverList;
    }
}
