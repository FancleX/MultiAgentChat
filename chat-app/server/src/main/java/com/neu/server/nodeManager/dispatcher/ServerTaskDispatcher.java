package com.neu.server.nodeManager.dispatcher;

import com.neu.liveNodeList.LiveNodeList;
import com.neu.liveNodeList.ServerLiveNodeListImpl;
import com.neu.protocol.TransmitProtocol;
import com.neu.node.Node;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Dispatch incoming message and classify by the message type,
 * call different apis to handler the events.
 */
@ChannelHandler.Sharable
public class ServerTaskDispatcher extends SimpleChannelInboundHandler<TransmitProtocol> {

    // store metadata of the connections
    private final LiveNodeList<Node> nodeList = new ServerLiveNodeListImpl<>();

    /**
     * Message read in with the io channel of the sender.
     *
     * @param ctx           the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *                      belongs to
     * @param msg           the message to handle
     * @throws Exception any exception thrown from the method will be caught at exceptionCaught method.
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TransmitProtocol msg) throws Exception {
        System.out.println("msg");
        switch (msg.getType()) {
            // TODO: dispatch task by types
        }
    }

    /**
     * A node connection established, should have further handlers to get the metadata of from the node,
     * before add the node to the live node list.
     *
     * @param ctx the channel io of the node
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    /**
     * A node broke or lost connection.
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    /**
     * Exceptions caught here.
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
