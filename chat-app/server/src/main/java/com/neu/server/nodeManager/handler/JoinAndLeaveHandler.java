package com.neu.server.nodeManager.handler;

import com.neu.handlerAPI.GeneralEventHandlerAPI;
import com.neu.protocol.joinAndLeaveProtocol.JoinAndLeaveProtocol;
import com.neu.server.sharableResource.SharableResource;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class JoinAndLeaveHandler implements GeneralEventHandlerAPI<JoinAndLeaveProtocol> {

    public JoinAndLeaveHandler() {}

    @Override
    public void handle(JoinAndLeaveProtocol protocol, ChannelHandlerContext ctx) {
        switch (protocol.getSubType()) {
            case JOIN:
                SharableResource.liveNodeList.add(protocol.getNodeInfo());
                log.info("A new node joined: " + protocol.getNodeInfo());
                break;
            case LEAVE:
                SharableResource.liveNodeList.remove(protocol.getNodeInfo().getId());
                log.info("A node left: " + protocol.getNodeInfo());
                // logout the node
                new RestTemplate().postForEntity("http://localhost:" + SharableResource.myHttpPort + "/user/logout", protocol.getNodeInfo().getId(), Void.class);
                break;
        }
    }
}
