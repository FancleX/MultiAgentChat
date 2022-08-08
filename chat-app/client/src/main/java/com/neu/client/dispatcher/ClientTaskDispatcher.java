package com.neu.client.dispatcher;


import com.neu.liveNodeList.ClientLiveNodeListImpl;
import com.neu.liveNodeList.LiveNodeList;
import com.neu.node.NodeChannel;
import com.neu.protocol.TransmitProtocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Iterator;

@ChannelHandler.Sharable
public class ClientTaskDispatcher extends SimpleChannelInboundHandler<TransmitProtocol> {

    private NodeChannel myNode;

    // to determine if my node is the leader
    private boolean isLeader;

    private LiveNodeList<NodeChannel> liveNodeList = new ClientLiveNodeListImpl<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TransmitProtocol msg) throws Exception {
        System.out.println("msg");
        switch (msg.getType()) {
            // TODO: dispatch task by types
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("A node connected");
    }
}
