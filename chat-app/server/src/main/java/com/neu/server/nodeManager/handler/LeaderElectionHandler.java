package com.neu.server.nodeManager.handler;

import com.neu.liveNodeList.LiveNodeList;
import com.neu.node.Node;
import com.neu.node.NodeChannel;
import com.neu.p2pConnectionGroup.P2PConnectionGroup;
import com.neu.protocol.GeneralType;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionProtocol;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionType;
import com.neu.server.nodeManager.NodeManager;
import com.neu.server.nodeManager.dispatcher.ServerTaskDispatcher;
import com.neu.server.tokenGenerator.TokenGenerator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class LeaderElectionHandler {

    private final LiveNodeList<Node> nodeList;

    public LeaderElectionHandler(LiveNodeList<Node> nodeList) {
        this.nodeList = nodeList;
    }

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
                Node leaderNode = nodeList.getLeaderNode();
                if (leaderNode.getId().equals(id) && leaderNode.getHostname().equals(hostname) && leaderNode.getPort() == port) {
                    // verify correctly, remove the current leader node
                    nodeList.remove(leaderNode.getId());
                    ServerTaskDispatcher.setLeaderNode(null);
                    ctx.channel().close();
                }
                log.info("Leader node: " + leaderNode + " exited");
                break;
            case CLIENT_REPORT:
                // get the new leader node
                Node newLeader = leaderElectionProtocol.getNodeInfo();
                newLeader.setLeader(true);
                log.info("A new leader reported: " + newLeader);
                // add to the list
                nodeList.add(newLeader);
                // generate a token for the node
                String token = TokenGenerator.generateToken(newLeader.getId(), newLeader.getHostname(), newLeader.getPort());
                // connect to the new leader node
                Channel leaderChannel = null;
                try {
                    leaderChannel = NodeManager.getP2PConnectionGroup().connect(newLeader.getHostname(), newLeader.getPort());
                } catch (SocketTimeoutException e) {
                    log.error("Failed to connect to the new leader node: " + newLeader);
                    log.warn("Retry to connect to the new leader node: " + newLeader);
                    // retry once
                    try {
                        leaderChannel = NodeManager.getP2PConnectionGroup().connect(newLeader.getHostname(), newLeader.getPort());
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
                ServerTaskDispatcher.setLeaderNode(new NodeChannel(newLeader, leaderChannel));
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
        if (nodeList.size() == 0) {
            return null;
        }
        Iterator<Node> allNodes = nodeList.getAllNodes();
        while (allNodes.hasNext()) {
            Node next = allNodes.next();
            try {
                return NodeManager.getP2PConnectionGroup().connect(next.getHostname(), next.getPort());
            } catch (SocketTimeoutException e) {
                log.error("Failed to connect to node: " + next);
            }
        }
        return null;
    }

}
