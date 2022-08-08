package com.neu.client.communication;

import com.neu.liveNodeList.LiveNodeList;
import com.neu.node.NodeChannel;
import com.neu.protocol.TransmitProtocol;

import java.util.Iterator;

public class CommunicationAPIImpl implements CommunicationAPI {

    private final LiveNodeList<NodeChannel> liveNodeList;

    public CommunicationAPIImpl(LiveNodeList<NodeChannel> liveNodeList) {
        this.liveNodeList = liveNodeList;
    }


    @Override
    public void send(Long id, TransmitProtocol msg) {
        NodeChannel nodeChannel = liveNodeList.get(id);
        nodeChannel.getChannel().writeAndFlush(msg);
    }

    @Override
    public void broadcast(TransmitProtocol msg) {
        Iterator<NodeChannel> allNodes = liveNodeList.getAllNodes();
        while (allNodes.hasNext()) {
            NodeChannel next = allNodes.next();
            send(next.getId(), msg);
        }
    }
}
