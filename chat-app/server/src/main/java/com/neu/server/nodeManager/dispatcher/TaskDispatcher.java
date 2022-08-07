package com.neu.server.nodeManager.dispatcher;

import com.neu.liveNodeList.LiveNodeList;
import com.neu.liveNodeList.ServerLiveNodeListImpl;
import com.neu.protocol.TransmitProtocol;
import com.neu.server.nodeManager.node.Node;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Dispatch incoming message and classify by the message type,
 * call different apis to handler the events.
 */
public class TaskDispatcher extends SimpleChannelInboundHandler<TransmitProtocol> {

    private final LiveNodeList<Node> nodeList = new ServerLiveNodeListImpl<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TransmitProtocol msg) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
