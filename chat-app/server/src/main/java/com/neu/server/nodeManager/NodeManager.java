package com.neu.server.nodeManager;


import com.neu.liveNodeList.LiveNodeList;
import com.neu.liveNodeList.ServerLiveNodeListImpl;
import com.neu.p2pConnectionGroup.P2PConnectionGroup;
import com.neu.node.Node;
import com.neu.server.nodeManager.dispatcher.ServerTaskDispatcher;
import io.netty.channel.ChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Passively establish socket connection from the leader node.
 */
@Slf4j
public class NodeManager {

    private static LiveNodeList<Node> nodeList;

    private static P2PConnectionGroup p2PConnectionGroup;

    public static void start(int port) {
        nodeList = new ServerLiveNodeListImpl<>();
        p2PConnectionGroup = new P2PConnectionGroup(port, new ServerTaskDispatcher(nodeList));
    }

    public static LiveNodeList<Node> getNodeList() {
        return nodeList;
    }

    public static P2PConnectionGroup getP2PConnectionGroup() {
        return p2PConnectionGroup;
    }

}
