package com.neu.server.nodeManager.dispatcher;

import com.neu.protocol.GeneralType;
import com.neu.protocol.TransmitProtocol;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionProtocol;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionType;
import com.neu.server.nodeManager.handler.LeaderElectionHandler;
import com.neu.server.sharableResource.SharableResource;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

/**
 * Dispatch incoming message and classify by the message type,
 * call different apis to handler the events.
 */
@ChannelHandler.Sharable
@Slf4j
public class ServerTaskDispatcher extends SimpleChannelInboundHandler<TransmitProtocol> {

    private final LeaderElectionHandler leaderElectionHandler;

    public ServerTaskDispatcher() {
        this.leaderElectionHandler = new LeaderElectionHandler();
    }

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
            case LEADER_ELECTION:
                leaderElectionHandler.handle((LeaderElectionProtocol) msg, ctx);
                break;
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
        // leader node may crash
        if (ctx.channel().equals(SharableResource.leaderNode.getChannel())) {
            // set the leader as logged out status
            new RestTemplate().postForEntity("http://localhost:" + SharableResource.myHttpPort + "/user/logout", SharableResource.leaderNode.getId(), Void.class);
            log.info("The last leader node logged out triggered by the system");
            SharableResource.liveNodeList.remove(SharableResource.leaderNode.getId());

            log.info("Leader node lost the connection with server, a new round leader election will start");
            if (SharableResource.liveNodeList.size() != 0) {
                Channel channel = leaderElectionHandler.ConnectToNext();
                // start a new round leader election
                LeaderElectionProtocol leaderElectionRequest = new LeaderElectionProtocol(GeneralType.LEADER_ELECTION, LeaderElectionType.SERVER_REQUEST);
                log.info("Sent request: " + leaderElectionRequest + " to a node");
                channel.writeAndFlush(leaderElectionRequest);
            }
            log.info("No nodes are in the p2p network");
        }
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
