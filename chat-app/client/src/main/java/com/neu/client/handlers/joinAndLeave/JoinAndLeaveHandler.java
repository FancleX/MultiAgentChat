package com.neu.client.handlers.joinAndLeave;


import com.neu.client.handlers.transaction.TransactionAPI;
import com.neu.client.handlers.transaction.TransactionImpl;
import com.neu.client.sharableResource.SharableResource;
import com.neu.client.ui.UI;
import com.neu.handlerAPI.GeneralEventHandlerAPI;
import com.neu.node.NodeChannel;
import com.neu.protocol.GeneralType;
import com.neu.protocol.generalCommunicationProtocol.GeneralCommunicationProtocol;
import com.neu.protocol.generalCommunicationProtocol.GeneralCommunicationType;
import com.neu.protocol.joinAndLeaveProtocol.JoinAndLeaveProtocol;
import com.neu.protocol.joinAndLeaveProtocol.JoinAndLeaveType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketTimeoutException;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JoinAndLeaveHandler implements GeneralEventHandlerAPI<JoinAndLeaveProtocol> {

    private static final TransactionAPI transactionAPI = new TransactionImpl();

    private static final Queue<JoinAndLeaveProtocol> queue = new LinkedBlockingQueue<>();

    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public JoinAndLeaveHandler() {
        this.scheduler();
    }


    /**
     * Use for a new node none leader node to join the network. Called in login method.
     */
    public static void join(String leaderHostname, int leaderPort) throws SocketTimeoutException {
        JoinAndLeaveProtocol join = new JoinAndLeaveProtocol(GeneralType.JOIN_AND_LEAVE, JoinAndLeaveType.JOIN, SharableResource.myNode);
        Channel connect = SharableResource.group.connect(leaderHostname, leaderPort);
        log.info("Sent join request to the leader node: " + join);
        connect.writeAndFlush(join);
        connect.close();
    }

    /**
     * Use for the node to leave the network. Called in onExit method.
     */
    public static void leave() {
        // if the node is not leader, send message to the leader
        if (!SharableResource.myNode.isLeader()) {
            // send message to the leader node to report leave
            NodeChannel leaderNode = SharableResource.liveNodeList.getLeaderNode();
            JoinAndLeaveProtocol leave = new JoinAndLeaveProtocol(GeneralType.JOIN_AND_LEAVE, JoinAndLeaveType.LEAVE, SharableResource.myNode);
            log.info("Sent leave request to the leader node: " + leave);
            leaderNode.getChannel().writeAndFlush(leave);
            return;
        }
        // if the node is leader
        // start transaction
        queue.add(new JoinAndLeaveProtocol(GeneralType.JOIN_AND_LEAVE, JoinAndLeaveType.LEAVE, SharableResource.myNode));
    }

    public static void markCompleted() {
        executorService.notify();
    }

    @Override
    public void handle(JoinAndLeaveProtocol protocol, ChannelHandlerContext ctx) {
        // only leader node should take care of join and leave events of a node
            switch (protocol.getSubType()) {
                case JOIN:
                    if (SharableResource.myNode.isLeader()) {
                        if (SharableResource.liveNodeList.size() != 0) {
                            // enqueue the transaction
                            queue.add(protocol);
                            ctx.channel().close();
                        } else {
                            // if no nodes in the list, no transaction need
                            try {
                                Channel connect = SharableResource.group.connect(protocol.getNodeInfo().getHostname(), protocol.getNodeInfo().getPort());
                                SharableResource.liveNodeList.add(new NodeChannel(protocol.getNodeInfo(), connect));
                                connect.writeAndFlush(new JoinAndLeaveProtocol(GeneralType.JOIN_AND_LEAVE, JoinAndLeaveType.GREETING, SharableResource.myNode));
                                log.info("Sent GREETING response for join request of node: " + protocol.getNodeInfo());
                                // report to server
                                SharableResource.server.writeAndFlush(new JoinAndLeaveProtocol(GeneralType.JOIN_AND_LEAVE, JoinAndLeaveType.JOIN, protocol.getNodeInfo()));
                                log.info("Sent REPORT to server for node: " + protocol.getNodeInfo());
                            } catch (SocketTimeoutException ignored) {}
                        }
                    }
                    break;
                case LEAVE:
                    if (SharableResource.myNode.isLeader()) {
                        // enqueue the transaction
                        queue.add(protocol);
                    }
                    break;
                case GREETING:
                    if (SharableResource.liveNodeList.size() == 0) {
                        log.info("Joined to the p2p network");
                        UI.isJoined = true;
                    }
                    // store the node to local live node list
                    SharableResource.liveNodeList.add(new NodeChannel(protocol.getNodeInfo(), ctx.channel()));
                    break;
            }

    }

    private void scheduler() {
        executorService.scheduleAtFixedRate(() -> {
            while (!queue.isEmpty()) {
                JoinAndLeaveProtocol head = queue.poll();
                switch (head.getSubType()) {
                    case JOIN:
                        // start a transaction
                        log.info("Start a JOIN transaction for node: " + head.getNodeInfo());
                        transactionAPI.prepare(head.getNodeInfo(), JoinAndLeaveType.JOIN);
                        break;
                    case LEAVE:
                        // start a transaction
                        log.info("Start a LEAVE transaction for node: " + head.getNodeInfo());
                        transactionAPI.prepare(head.getNodeInfo(), JoinAndLeaveType.LEAVE);
                        break;
                }
                try {
                    synchronized (this) {
                        log.info("Thread in waiting for the transaction complete");
                        wait();
                    }
                } catch (InterruptedException ignored) {}
            }
        }, 300, 1000, TimeUnit.MILLISECONDS);
    }


}
