package com.neu.server.nodeManager;


import com.neu.liveNodeList.LiveNodeList;
import com.neu.p2pConnectionGroup.P2PConnectionGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * Passively establish socket connection from the leader node.
 */
@Slf4j
public class NodeManager {

    private final LiveNodeList nodeList;

    public NodeManager(int port) {
        this.nodeList = new LiveNodeList();
        new P2PConnectionGroup(port, nodeList);
    }

}
