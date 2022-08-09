package com.neu.server.nodeManager.dispatcher;

import com.neu.liveNodeList.LiveNodeList;
import com.neu.liveNodeList.ServerLiveNodeListImpl;
import com.neu.node.NodeChannel;
import com.neu.protocol.GeneralType;
import com.neu.protocol.TransmitProtocol;
import com.neu.node.Node;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionProtocol;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionType;
import com.neu.server.nodeManager.handler.LeaderElectionHandler;
import io.netty.channel.Channel;
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
    private final LiveNodeList<Node> nodeList;

    private static NodeChannel leaderNode;

    private final LeaderElectionHandler leaderElectionHandler;

    public ServerTaskDispatcher(LiveNodeList<Node> nodeList) {
        this.nodeList = nodeList;
        this.leaderElectionHandler = new LeaderElectionHandler(nodeList);
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
        if (ctx.channel().equals(leaderNode.getChannel())) {
            nodeList.remove(leaderNode.getId());
            Channel channel = leaderElectionHandler.ConnectToNext();
            // start a new round leader election
            LeaderElectionProtocol leaderElectionRequest = new LeaderElectionProtocol(GeneralType.LEADER_ELECTION, LeaderElectionType.SERVER_REQUEST);
            channel.writeAndFlush(leaderElectionRequest);
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

    public static NodeChannel getLeaderNode() {
        return leaderNode;
    }

    public static void setLeaderNode(NodeChannel leaderNode) {
        ServerTaskDispatcher.leaderNode = leaderNode;
    }
}
