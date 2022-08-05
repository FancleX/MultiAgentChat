package com.neu.p2pConnectionGroup;

import com.neu.liveNodeList.LiveNodeList;
import com.neu.p2pConnectionGroup.nettyClient.NettyClient;
import com.neu.p2pConnectionGroup.nettyServer.NettyServer;
import lombok.extern.slf4j.Slf4j;

/**
 * Start bi-directed peer to peer connections.
 */
@Slf4j
public class P2PConnectionGroup {

    /**
     * Construct a peer to peer connection group for actively start and passively receive the incoming connections.
     *
     * @param port the port of the server to be registered
     * @param nodeList the list that will store all live nodes' information
     */
    public P2PConnectionGroup(int port, LiveNodeList nodeList) {
        new NettyClient(nodeList);
        new NettyServer(port, nodeList);
        log.info("Peer to peer service started");
    }

}
