package com.neu.client.communication;

import com.neu.client.sharableResource.SharableResource;
import com.neu.node.NodeChannel;
import com.neu.protocol.TransmitProtocol;

import java.util.Iterator;

public class CommunicationAPIImpl implements CommunicationAPI {

    public CommunicationAPIImpl() {}

    @Override
    public void send(Long id, TransmitProtocol msg) {
        NodeChannel nodeChannel = SharableResource.liveNodeList.get(id);
        nodeChannel.getChannel().writeAndFlush(msg);
    }

    @Override
    public void broadcast(TransmitProtocol msg) {
        Iterator<NodeChannel> allNodes = SharableResource.liveNodeList.getAllNodes();
        while (allNodes.hasNext()) {
            NodeChannel next = allNodes.next();
            send(next.getId(), msg);
        }
    }
}
