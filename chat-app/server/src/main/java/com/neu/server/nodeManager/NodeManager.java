package com.neu.server.nodeManager;

import com.neu.server.sharableResource.SharableResource;
import lombok.extern.slf4j.Slf4j;

/**
 * Passively establish socket connection from the leader node.
 */
@Slf4j
public class NodeManager {

    public NodeManager(int port) {
        SharableResource.init(port);
    }

}
