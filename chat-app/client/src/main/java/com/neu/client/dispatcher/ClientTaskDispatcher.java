package com.neu.client.dispatcher;


import com.neu.client.handlers.generalCommunication.GeneralCommunicationHandler;
import com.neu.client.handlers.joinAndLeave.JoinAndLeaveHandler;
import com.neu.client.handlers.leaderElection.LeaderElectionHandler;
import com.neu.client.handlers.transaction.TransactionHandler;
import com.neu.client.sharableResource.SharableResource;
import com.neu.handlerAPI.GeneralEventHandlerAPI;
import com.neu.node.NodeChannel;
import com.neu.protocol.TransmitProtocol;
import com.neu.protocol.generalCommunicationProtocol.GeneralCommunicationProtocol;
import com.neu.protocol.joinAndLeaveProtocol.JoinAndLeaveProtocol;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionProtocol;
import com.neu.protocol.transactionProtocol.TransactionProtocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@ChannelHandler.Sharable
public class ClientTaskDispatcher extends SimpleChannelInboundHandler<TransmitProtocol> {


    private final GeneralEventHandlerAPI<LeaderElectionProtocol> leaderElectionHandler;

    private final GeneralEventHandlerAPI<GeneralCommunicationProtocol> generalCommunicationHandler;

    private final GeneralEventHandlerAPI<JoinAndLeaveProtocol> joinAndLeaveHandler;

    private final GeneralEventHandlerAPI<TransactionProtocol> transactionEventHandler;

    public ClientTaskDispatcher() {
        this.leaderElectionHandler = new LeaderElectionHandler();
        this.generalCommunicationHandler = new GeneralCommunicationHandler();
        this.joinAndLeaveHandler = new JoinAndLeaveHandler();
        this.transactionEventHandler = new TransactionHandler();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TransmitProtocol msg) throws Exception {
        switch (msg.getType()) {
            // dispatch task by types
            case LEADER_ELECTION:
                leaderElectionHandler.handle((LeaderElectionProtocol) msg, ctx);
                break;
            case GENERAL_COMMUNICATION:
                generalCommunicationHandler.handle((GeneralCommunicationProtocol) msg, ctx);
                break;
            case JOIN_AND_LEAVE:
                joinAndLeaveHandler.handle((JoinAndLeaveProtocol) msg, ctx);
                break;
            case TRANSACTION:
                transactionEventHandler.handle((TransactionProtocol) msg, ctx);
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // if the leader node crash
        // the leader node doesn't exit by transaction
        NodeChannel leaderNode = SharableResource.liveNodeList.getLeaderNode();
        if (SharableResource.liveNodeList.size() != 0 && leaderNode != null) {
            if (ctx.channel().equals(leaderNode.getChannel())) {
                log.info("Detected leader node crash, crash will be handled by server");
                SharableResource.liveNodeList.remove(leaderNode.getId());
            }
        }
        log.info("Channel: " + ctx.channel() + " break the connection");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage());
    }
}
