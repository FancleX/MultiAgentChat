package com.neu.server.sharableResource;

import com.neu.liveNodeList.LiveNodeList;
import com.neu.liveNodeList.ServerLiveNodeListImpl;
import com.neu.node.Node;
import com.neu.node.NodeChannel;
import com.neu.p2pConnectionGroup.P2PConnectionGroup;
import com.neu.server.nodeManager.dispatcher.ServerTaskDispatcher;

/**
 * Static resource shared with all system modules.
 */
public final class SharableResource {

    public static LiveNodeList<Node> liveNodeList;

    public static P2PConnectionGroup group;

    public static NodeChannel leaderNode;

    public static int myHttpPort;

    public static int myPort;

    public static void init(int port) {
        liveNodeList = new ServerLiveNodeListImpl<>();
        group = new P2PConnectionGroup(port, new ServerTaskDispatcher());
    }

}
