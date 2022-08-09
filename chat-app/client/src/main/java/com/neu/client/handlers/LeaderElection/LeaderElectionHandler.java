package com.neu.client.handlers.LeaderElection;

import com.neu.liveNodeList.LiveNodeList;
import com.neu.node.Node;
import com.neu.node.NodeChannel;
import com.neu.p2pConnectionGroup.P2PConnectionGroup;
import com.neu.protocol.GeneralType;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionProtocol;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.net.SocketTimeoutException;

@Slf4j
public class LeaderElectionHandler {

    @Value("${netty.server.hostname}")
    private String serverHostname;

    @Value("${netty.server.port}")
    private int serverPort;

    private Node myNode;

    // to determine if my node is the leader
    private String leaderNodeToken;

    private final LiveNodeList<NodeChannel> liveNodeList;

    private final P2PConnectionGroup group;

    private Channel server;

    public LeaderElectionHandler(LiveNodeList<NodeChannel> liveNodeList, P2PConnectionGroup group) {
        this.liveNodeList = liveNodeList;
        this.group = group;
    }

    public void handler(LeaderElectionProtocol leaderElectionProtocol, ChannelHandlerContext ctx) {
        switch (leaderElectionProtocol.getSubType()) {
            case SERVER_REQUEST:
                // TODO: start leader election
                break;
            case SERVER_AUTH:
                String leaderToken = leaderElectionProtocol.getLeaderToken();
                myNode.setLeader(true);
                leaderNodeToken = leaderToken;
                break;
            case LEADER_REQUEST:
                break;
            case NODE_REPORT:
                break;
        }
    }



}
