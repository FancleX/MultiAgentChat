package com.neu.client.dispatcher;


import com.neu.client.handlers.generalCommunication.GeneralCommunicationHandler;
import com.neu.client.handlers.leaderElection.LeaderElectionHandler;
import com.neu.handlerAPI.GeneralEventHandlerAPI;
import com.neu.protocol.TransmitProtocol;
import com.neu.protocol.generalCommunicationProtocol.GeneralCommunicationProtocol;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionProtocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class ClientTaskDispatcher extends SimpleChannelInboundHandler<TransmitProtocol> {


    private final GeneralEventHandlerAPI<LeaderElectionProtocol> leaderElectionHandler;

    private final GeneralEventHandlerAPI<GeneralCommunicationProtocol> generalCommunicationHandler;

    public ClientTaskDispatcher() {
        this.leaderElectionHandler = new LeaderElectionHandler();
        this.generalCommunicationHandler = new GeneralCommunicationHandler();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TransmitProtocol msg) throws Exception {
        System.out.println(msg.getClass().getName());
        switch (msg.getType()) {
            // dispatch task by types
            case LEADER_ELECTION:
                leaderElectionHandler.handle((LeaderElectionProtocol) msg, ctx);
                break;
            case GENERAL_COMMUNICATION:
                generalCommunicationHandler.handle((GeneralCommunicationProtocol) msg, ctx);
                break;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("A node connected");
    }
}
