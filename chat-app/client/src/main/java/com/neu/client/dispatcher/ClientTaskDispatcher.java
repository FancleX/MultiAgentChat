package com.neu.client.dispatcher;

import com.neu.protocol.TransmitProtocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class ClientTaskDispatcher extends SimpleChannelInboundHandler<TransmitProtocol> {
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
