package com.neu.server.nodeManager.handler;

import com.neu.handlerAPI.GeneralEventHandlerAPI;
import com.neu.node.Node;
import com.neu.node.NodeChannel;
import com.neu.protocol.GeneralType;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionProtocol;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionType;
import com.neu.server.sharableResource.SharableResource;
import com.neu.server.tokenGenerator.TokenGenerator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class LeaderElectionHandler implements GeneralEventHandlerAPI<LeaderElectionProtocol> {

    public LeaderElectionHandler() {}

    @Override
    public void handle(LeaderElectionProtocol leaderElectionProtocol, ChannelHandlerContext ctx) {
        switch (leaderElectionProtocol.getSubType()) {
            case TOKEN_RETURN:
                log.info("Leader node is returning the leader token: " + leaderElectionProtocol.getNodeInfo() + ", token: " + leaderElectionProtocol.getLeaderToken());
                String leaderToken = leaderElectionProtocol.getLeaderToken();
                // verify the token if it is the right leader
                Map<String, Object> verify = TokenGenerator.verify(leaderToken);
                if (verify == null) {
                    log.error("Failed to verify the leader token: " + leaderElectionProtocol.getNodeInfo() + ", token: " + leaderElectionProtocol.getLeaderToken());
                    // incorrect token
                    // close the connection
                    ctx.channel().close();
                    return;
                }
                // compare the node information
                Long id = (Long) verify.get("id");
                String hostname = (String) verify.get("hostname");
                int port = (int) verify.get("port");
                // get the leader node
                NodeChannel leaderNode = SharableResource.leaderNode;
                if (leaderNode.getId().equals(id) && leaderNode.getHostname().equals(hostname) && leaderNode.getPort() == port) {
                    // verify correctly, remove the current leader node
                    SharableResource.liveNodeList.remove(leaderNode.getId());
                    SharableResource.leaderNode = null;
                    // logout the last leader node
                    new RestTemplate().postForEntity("http://localhost:" + SharableResource.myHttpPort + "/user/logout", id, Void.class);
                    ctx.channel().close();
                }
                log.info("Leader node: " + leaderNode + " exited");
                // start leader election after the leader node exited if the live node list is not empty
                if (SharableResource.liveNodeList.size() == 0) {
                    log.info("No nodes are in the p2p network");
                    return;
                }
                Channel next = ConnectToNext();
                next.writeAndFlush(new LeaderElectionProtocol(GeneralType.LEADER_ELECTION, LeaderElectionType.SERVER_REQUEST));
                break;
            case CLIENT_REPORT:
                // get and update the new leader node
                Node newLeader = leaderElectionProtocol.getNodeInfo();
                // set current leader to false if it has
                if (SharableResource.liveNodeList.size() != 0) {
                    if (SharableResource.liveNodeList.getLeaderNode() != null) {
                        Node oldLeader = SharableResource.liveNodeList.getLeaderNode();
                        oldLeader.setLeader(false);

                        Node node = SharableResource.liveNodeList.get(newLeader.getId());
                        node.setLeader(true);
                        newLeader = node;
                    } else {
                        // query the current node list and set the node to leader
                        Node node = SharableResource.liveNodeList.get(newLeader.getId());
                        node.setLeader(true);
                    }

                } else {
                    // if empty then add the node
                    newLeader.setLeader(true);
                    SharableResource.liveNodeList.add(newLeader);
                }

                log.info("A new leader reported: " + newLeader);
                // close the current connection
                ctx.channel().close();
                // generate a token for the node
                String token = TokenGenerator.generateToken(newLeader.getId(), newLeader.getHostname(), newLeader.getPort());
                // connect to the new leader node
                Channel leaderChannel = null;
                try {
                    leaderChannel = SharableResource.group.connect(newLeader.getHostname(), newLeader.getPort());
                } catch (SocketTimeoutException e) {
                    log.error("Failed to connect to the new leader node: " + newLeader);
                    log.warn("Retry to connect to the new leader node: " + newLeader);
                    // retry once
                    try {
                        leaderChannel = SharableResource.group.connect(newLeader.getHostname(), newLeader.getPort());
                    } catch (SocketTimeoutException ex) {
                        log.error("Failed to connect the new leader node: " + newLeader + " after retry");
                        // ask another node to start new round leader election
                        Channel channel = ConnectToNext();
                        if (channel != null) {
                            log.info("Leader election request sent to another node");
                            LeaderElectionProtocol newRequest = new LeaderElectionProtocol(GeneralType.LEADER_ELECTION, LeaderElectionType.SERVER_REQUEST);
                            channel.writeAndFlush(newRequest);
                        } else {
                            // all nodes are unresponsive report system issue
                            log.error("All nodes are unresponsive, please check system issue");
                            return;
                        }
                    }
                }
                assert leaderChannel != null;
                // send token to the new leader node
                LeaderElectionProtocol newLeaderMessage = new LeaderElectionProtocol(GeneralType.LEADER_ELECTION, LeaderElectionType.SERVER_AUTH, token);
                leaderChannel.writeAndFlush(newLeaderMessage);
                SharableResource.leaderNode = new NodeChannel(newLeader, leaderChannel);
                log.info("Sent token to new leader node: " + newLeader + ", token: " + newLeaderMessage);
                break;
        }
    }

    /**
     * Keep connect to the next node until the connection is established.
     *
     * @return the io channel of the connected node, if run out of all tries or empty node list return null
     */
    public Channel ConnectToNext() {
        if (SharableResource.liveNodeList.size() == 0) {
            return null;
        }
        Iterator<Node> allNodes = SharableResource.liveNodeList.getAllNodes();
        while (allNodes.hasNext()) {
            Node next = allNodes.next();
            try {
                return SharableResource.group.connect(next.getHostname(), next.getPort());
            } catch (SocketTimeoutException e) {
                log.error("Failed to connect to node: " + next);
            }
        }
        return null;
    }

}
