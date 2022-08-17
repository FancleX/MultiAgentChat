package com.neu.client.handlers.leaderElection;

import com.neu.client.communication.CommunicationAPIImpl;
import com.neu.client.sharableResource.SharableResource;
import com.neu.handlerAPI.GeneralEventHandlerAPI;
import com.neu.node.Node;
import com.neu.node.NodeChannel;
import com.neu.protocol.GeneralType;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionProtocol;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionType;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class LeaderElectionHandler implements GeneralEventHandlerAPI<LeaderElectionProtocol> {


    private final Map<Node, Integer> nodeReportsCollector;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public LeaderElectionHandler() {
        this.nodeReportsCollector = new HashMap<>();
        // an asynchronously thread will handle the next, if the collect from all nodes
        this.performanceAnalyzer();
    }

    @Override
    public void handle(LeaderElectionProtocol leaderElectionProtocol, ChannelHandlerContext ctx) {
        switch (leaderElectionProtocol.getSubType()) {
            case SERVER_REQUEST:
                // start leader election and report the metadata of the leader node
                SharableResource.server = ctx.channel();
                LeaderElectionProtocol serverRequest = new LeaderElectionProtocol(GeneralType.LEADER_ELECTION, LeaderElectionType.LEADER_REQUEST);
                log.info("Received request from server: " + serverRequest);
                // if empty list send self
                if (SharableResource.liveNodeList.size() == 0) {
                    SharableResource.server.writeAndFlush(new LeaderElectionProtocol(GeneralType.LEADER_ELECTION, LeaderElectionType.CLIENT_REPORT, SharableResource.myNode));
                    SharableResource.server = null;
                    return;
                }
                startLeaderElection(serverRequest);
                break;
            case SERVER_AUTH:
                log.info("Received " + leaderElectionProtocol.getSubType() + " from server, this node has become the leader node");
                String leaderToken = leaderElectionProtocol.getLeaderToken();
                SharableResource.myNode.setLeader(true);
                SharableResource.leaderNodeToken = leaderToken;
                SharableResource.server = ctx.channel();
                // broadcast to all nodes
                new CommunicationAPIImpl().broadcast(new LeaderElectionProtocol(GeneralType.LEADER_ELECTION, LeaderElectionType.LEADER_CHOSEN, SharableResource.myNode));
                break;
            case LEADER_REQUEST:
                // send request to all nodes
                log.info("Received " + leaderElectionProtocol.getSubType() + " from leader node");
                // send my performance weight to the node who started the leader election
                int performance = getPerformance();
                LeaderElectionProtocol report = new LeaderElectionProtocol(GeneralType.LEADER_ELECTION, LeaderElectionType.NODE_REPORT, SharableResource.myNode, performance);
                ctx.channel().writeAndFlush(report);
                break;
            case NODE_REPORT:
                // collect nodes response
                log.info("Received " + leaderElectionProtocol.getSubType() + " from" + leaderElectionProtocol.getNodeInfo() + " with performance points " + leaderElectionProtocol.getPerformanceWeight());
                // collect nodes report
                nodeReportsCollector.put(leaderElectionProtocol.getNodeInfo(), leaderElectionProtocol.getPerformanceWeight());
                break;
            case LEADER_CHOSEN:
                log.info("A leader reported: " + leaderElectionProtocol.getNodeInfo());
                NodeChannel nodeChannel = SharableResource.liveNodeList.get(leaderElectionProtocol.getNodeInfo().getId());
                nodeChannel.setLeader(true);
                break;
        }
    }


    /**
     * Only call to send LEADER_REQUEST to start the leader election.
     *
     * @param request the request from server or leader
     */
    public void startLeaderElection(LeaderElectionProtocol request) {
        Iterator<NodeChannel> allNodes = SharableResource.liveNodeList.getAllNodes();
        while (allNodes.hasNext()) {
            NodeChannel next = allNodes.next();
            next.getChannel().writeAndFlush(request);
            log.info("Sent request: " + request + " to node: " + next.getId());
        }
    }

    public void performanceAnalyzer() {
        executorService.scheduleAtFixedRate(() -> {
            if (nodeReportsCollector.size() == SharableResource.liveNodeList.size() && SharableResource.liveNodeList.size() != 0) {
                // add self performance
                nodeReportsCollector.put(SharableResource.myNode, getPerformance());
                // analyze which node has the lowest performance point
                Map.Entry<Node, Integer> theBestNode = getTheBestNode();
                // send the new leader node information to the server
                LeaderElectionProtocol leader = new LeaderElectionProtocol(GeneralType.LEADER_ELECTION, LeaderElectionType.CLIENT_REPORT, theBestNode.getKey());
                SharableResource.server.writeAndFlush(leader);
                // server will close the channel
                SharableResource.server = null;
                nodeReportsCollector.clear();
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    public Map.Entry<Node, Integer> getTheBestNode() {
        List<Map.Entry<Node, Integer>> collect = nodeReportsCollector.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).collect(Collectors.toList());
        return collect.get(0);
    }


    /**
     * Get current system performance based on the factors on cpu load, JVM memory.
     *
     * @return an overall weighted points from the system performance in interval 0 - 100, the lower, the better, which means system is less loaded.
     */
    public int getPerformance() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        // 35%
        double systemCpuLoad = osBean.getSystemCpuLoad();

        // in unit MB
        // 40%
        long freeMemory = Runtime.getRuntime().freeMemory() / 1024 * 1024;
        long maxMemory = Runtime.getRuntime().maxMemory();

        // 5%
        int availableProcessors = osBean.getAvailableProcessors();

        // 20%
        double systemLoadAverage = osBean.getSystemLoadAverage();

        int result;
        // if the program cannot get cpu usage due to privileges
        if (systemCpuLoad < 0 || systemLoadAverage < 0) {
            log.warn("Cannot get cpu or system loads due to the limited privileges");
            // consider the load is maximum by default
            result = (int) (35 + (freeMemory / maxMemory) * 40 + (1 - availableProcessors / 8) * 5 + 20);
        } else {
            log.info("System cpu load: {}% ", systemCpuLoad * 100);
            log.info("JVM free memory: {}MB", freeMemory);
            log.info("JVM max memory: {}MB", maxMemory);
            log.info("System available processors: " + availableProcessors);
            log.info("System average load: {}%", systemLoadAverage * 100);
            // call calculate the points
            result = (int) (systemCpuLoad * 35 + (freeMemory / maxMemory) * 40 + (1 - availableProcessors / 8) * 5 + systemLoadAverage * 20);
        }
        log.info("My system performance point: " + result);
        return result;
    }

}
