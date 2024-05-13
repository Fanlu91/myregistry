package com.flhai.myregistry.cluster;

import com.flhai.myregistry.http.HttpInvoker;
import com.flhai.myregistry.service.MyRegistryService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class ServerHealth {

    Cluster cluster;
    final private int interval = 5;

    public ServerHealth(Cluster cluster) {
        this.cluster = cluster;
    }

    public void checkServerHealth() {
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

        }, interval, interval, TimeUnit.SECONDS);
    }

    private void syncSnapshotFromLeader() {
        log.info("==> sync snapshot from leader.");
        log.debug("leader: {}, cluster.MYSELF: {}", cluster.leader(), cluster.MYSELF.isLeader());
        log.debug("leader version: {}, my version: {}", cluster.leader().getVersion(), cluster.MYSELF.getVersion());
        if (!cluster.MYSELF.isLeader() && cluster.MYSELF.getVersion() < cluster.leader().getVersion()) {
            log.info("sync snapshot from leader: {}", cluster.leader());
            Snapshot snapshot = HttpInvoker.httpGet(cluster.leader().getUrl() + "/snapshot", Snapshot.class);
            log.info("restore snapshot: {}", snapshot);
            MyRegistryService.restore(snapshot);
        }
    }

    /**
     * elect leader
     */
    private void electLeader() {
        List<Server> leaders = cluster.serverList.stream().filter(Server::isStatus).filter(Server::isLeader).collect(Collectors.toList());
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
        for (Server server : cluster.serverList) {
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
//        cluster.MYSELF.setLeader(candidate != null && candidate.equals(cluster.MYSELF));
    }

    /**
     * get and update all available servers
     */
    private void updateServers() {
        cluster.serverList.stream().parallel().forEach(server -> {
            try {
                if (server.equals(cluster.MYSELF)) {
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
}
