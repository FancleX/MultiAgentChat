package com.neu.client.handlers.transaction;

import com.neu.client.handlers.joinAndLeave.JoinAndLeaveHandler;
import com.neu.client.sharableResource.SharableResource;
import com.neu.handlerAPI.GeneralEventHandlerAPI;
import com.neu.node.Node;
import com.neu.node.NodeChannel;
import com.neu.protocol.GeneralType;
import com.neu.protocol.joinAndLeaveProtocol.JoinAndLeaveProtocol;
import com.neu.protocol.joinAndLeaveProtocol.JoinAndLeaveType;
import com.neu.protocol.transactionProtocol.TransactionProtocol;
import com.neu.protocol.transactionProtocol.TransactionType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketTimeoutException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TransactionHandler implements GeneralEventHandlerAPI<TransactionProtocol> {

    private final TransactionAPI transactionAPI;

    private TransactionProtocol currentNodeInTransaction;

    private int countAccept = 0;
    private int countAbort = 0;
    private int countACKCommit = 0;
    private int countACKDrop = 0;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    public TransactionHandler() {
        this.transactionAPI = new TransactionImpl();
        this.phase1Analyzer();
        this.phase2Analyzer();
    }

    @Override
    public void handle(TransactionProtocol protocol, ChannelHandlerContext ctx) {
        switch (protocol.getSubType()) {
            case PREPARE:
                currentNodeInTransaction = new TransactionProtocol(GeneralType.JOIN_AND_LEAVE, protocol.getMainType(), protocol.getNodeInfo());
                log.info("Current node in transaction: " + currentNodeInTransaction);
                switch (protocol.getMainType()) {
                    case JOIN:
                        boolean isContain = SharableResource.liveNodeList.isContain(protocol.getNodeInfo().getId());
                        if (isContain) {
                            transactionAPI.abort(ctx.channel());
                        } else {
                            transactionAPI.accept(ctx.channel());
                        }
                        break;
                    case LEAVE:
                        boolean isExisted = SharableResource.liveNodeList.isContain(protocol.getNodeInfo().getId());
                        if (isExisted) {
                            transactionAPI.accept(ctx.channel());
                        } else {
                            transactionAPI.abort(ctx.channel());
                        }
                        break;
                }
                break;
            case ACCEPT:
                if (SharableResource.myNode.isLeader()) {
                    log.info("Received accept");
                    countAccept++;
                }
                break;
            case ABORT:
                if (SharableResource.myNode.isLeader()) {
                    log.info("Received abort");
                    countAbort++;
                }
                break;
            case COMMIT:
                log.info("Committing the transaction");
                if (JoinAndLeaveType.JOIN.equals(protocol.getMainType())) {
                    connectTo(protocol);
                } else if (JoinAndLeaveType.LEAVE.equals(protocol.getMainType())) {
                    log.info("Broke connection with the node: " + protocol.getNodeInfo());
                    SharableResource.liveNodeList.remove(protocol.getNodeInfo().getId());
                }
                break;
            case DROP:
                log.info("Drop the transaction");
                // do nothing
                break;
            case ACK_COMMIT:
                if (SharableResource.myNode.isLeader()) {
                    log.info("Received ack commit");
                    countACKCommit++;
                }
                break;
            case ACK_DROP:
                if (SharableResource.myNode.isLeader()) {
                    log.info("Received ack drop");
                    countACKDrop++;
                }
                break;
        }
    }

    private void resetPhase1Counter() {
        countAccept = 0;
        countAbort = 0;
    }

    private void resetPhase2Counter() {
        countACKCommit = 0;
        countACKDrop = 0;
        currentNodeInTransaction = null;
        // tell the handler the transaction has done
        JoinAndLeaveHandler.markCompleted();
    }


    private void phase1Analyzer() {
        executorService.scheduleAtFixedRate(() -> {
            // exclude the current node in transaction and self
            while (countAccept + countAbort == SharableResource.liveNodeList.size() - 2) {
                if (countAbort > 0) {
                    // send drop
                    transactionAPI.drop(currentNodeInTransaction.getNodeInfo(), currentNodeInTransaction.getMainType());
                    resetPhase1Counter();
                    break;
                }
                // check self
                if (currentNodeInTransaction.getMainType().equals(JoinAndLeaveType.JOIN)) {
                    if (SharableResource.liveNodeList.isContain(currentNodeInTransaction.getNodeInfo().getId())) {
                        // drop
                        transactionAPI.drop(currentNodeInTransaction.getNodeInfo(), currentNodeInTransaction.getMainType());
                    } else {
                        // commit
                        transactionAPI.commit(currentNodeInTransaction.getNodeInfo(), currentNodeInTransaction.getMainType());
                    }
                } else if (currentNodeInTransaction.getMainType().equals(JoinAndLeaveType.LEAVE)) {
                    if (SharableResource.liveNodeList.isContain(currentNodeInTransaction.getNodeInfo().getId())) {
                        // commit
                        transactionAPI.commit(currentNodeInTransaction.getNodeInfo(), currentNodeInTransaction.getMainType());
                    } else {
                        // drop
                        transactionAPI.drop(currentNodeInTransaction.getNodeInfo(), currentNodeInTransaction.getMainType());
                    }
                }
                resetPhase1Counter();
            }
        }, 300, 1000, TimeUnit.MILLISECONDS);
    }

    private void phase2Analyzer() {
        executorService.scheduleAtFixedRate(() -> {
            // exclude the current node in transaction and self
            while (countACKCommit == SharableResource.liveNodeList.size() - 2 || countACKDrop == SharableResource.liveNodeList.size() - 2) {
                // do action on self
                if (currentNodeInTransaction.getMainType().equals(JoinAndLeaveType.JOIN)) {
                    connectTo(currentNodeInTransaction);
                    // report to server
                    SharableResource.server.writeAndFlush(new JoinAndLeaveProtocol(GeneralType.JOIN_AND_LEAVE, JoinAndLeaveType.JOIN, currentNodeInTransaction.getNodeInfo()));
                } else if (currentNodeInTransaction.getMainType().equals(JoinAndLeaveType.LEAVE)) {
                    // check if not self
                    if (!currentNodeInTransaction.getNodeInfo().getId().equals(SharableResource.myNode.getId())) {
                        log.info("Broke connection with the node: " + currentNodeInTransaction.getNodeInfo());
                        SharableResource.liveNodeList.remove(currentNodeInTransaction.getNodeInfo().getId());
                        // report to server
                        SharableResource.server.writeAndFlush(new JoinAndLeaveProtocol(GeneralType.JOIN_AND_LEAVE, JoinAndLeaveType.LEAVE, currentNodeInTransaction.getNodeInfo()));
                    }
                }
                resetPhase2Counter();
            }
        }, 300, 1000, TimeUnit.MILLISECONDS);
    }

    private void connectTo(TransactionProtocol currentNodeInTransaction) {
        try {
            Channel connect = SharableResource.group.connect(currentNodeInTransaction.getNodeInfo().getHostname(), currentNodeInTransaction.getNodeInfo().getPort());
            // add to live node list
            SharableResource.liveNodeList.add(new NodeChannel(currentNodeInTransaction.getNodeInfo(), connect));
            log.info("Established connection with a new node: " + currentNodeInTransaction.getNodeInfo());
            // send greeting message
            JoinAndLeaveProtocol greeting = new JoinAndLeaveProtocol(GeneralType.JOIN_AND_LEAVE, JoinAndLeaveType.GREETING, SharableResource.myNode);
            connect.writeAndFlush(greeting);
        } catch (SocketTimeoutException ignored) {
            // the exception shouldn't be happened since the node that requested join and leave should keep connection with the leader node
        }
    }
}
