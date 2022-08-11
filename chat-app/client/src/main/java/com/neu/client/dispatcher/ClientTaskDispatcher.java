package com.neu.client.dispatcher;


import com.neu.client.driver.ClientDriver;
import com.neu.client.handlers.LeaderElection.LeaderElectionHandler;
import com.neu.liveNodeList.ClientLiveNodeListImpl;
import com.neu.liveNodeList.LiveNodeList;
import com.neu.node.NodeChannel;
import com.neu.p2pConnectionGroup.P2PConnectionGroup;
import com.neu.protocol.TransmitProtocol;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionProtocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class ClientTaskDispatcher extends SimpleChannelInboundHandler<TransmitProtocol> {


    private final LeaderElectionHandler leaderElectionHandler;


    public ClientTaskDispatcher() {
        this.leaderElectionHandler = new LeaderElectionHandler();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TransmitProtocol msg) throws Exception {
        System.out.println(msg.getClass().getName());
        switch (msg.getType()) {
            // dispatch task by types
            case LEADER_ELECTION:
                leaderElectionHandler.handler((LeaderElectionProtocol) msg, ctx);
                break;

        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("A node connected");
    }
}
