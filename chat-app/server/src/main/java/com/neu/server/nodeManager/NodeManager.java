package com.neu.server.nodeManager;


import com.neu.liveNodeList.LiveNodeList;
import com.neu.liveNodeList.ServerLiveNodeListImpl;
import com.neu.p2pConnectionGroup.P2PConnectionGroup;
import com.neu.server.nodeManager.node.Node;
import io.netty.channel.ChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Passively establish socket connection from the leader node.
 */
@Slf4j
public class NodeManager {

    private final LiveNodeList<Node> nodeList;

    public NodeManager(int port, ChannelInboundHandler dispatcher) {
        this.nodeList = new ServerLiveNodeListImpl<>();
        new P2PConnectionGroup(port, dispatcher);
    }

}
