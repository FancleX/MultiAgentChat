package com.neu.client.communication;

import com.neu.client.sharableResource.SharableResource;
import com.neu.node.NodeChannel;
import com.neu.protocol.GeneralType;
import com.neu.protocol.TransmitProtocol;
import com.neu.protocol.generalCommunicationProtocol.GeneralCommunicationProtocol;
import com.neu.protocol.generalCommunicationProtocol.GeneralCommunicationType;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

@Slf4j
public class CommunicationAPIImpl implements CommunicationAPI {

    public CommunicationAPIImpl() {}

    @Override
    public void send(Long id, TransmitProtocol msg) {
        NodeChannel nodeChannel = SharableResource.liveNodeList.get(id);
        log.info("Sent message to id: " + id + ", message: " + msg);
        System.out.println(nodeChannel.getChannel());
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

//    @Override
//    public void broadcastExclude(TransmitProtocol msg, Long id) {
//        Iterator<NodeChannel> allNodes = SharableResource.liveNodeList.getAllNodes();
//        while (allNodes.hasNext()) {
//            NodeChannel next = allNodes.next();
//            if (!next.getId().equals(id)) {
//                log.info("Message sent to " + next.getNode());
//                send(id, msg);
//            }
//        }
//    }
}
