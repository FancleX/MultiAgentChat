package com.neu.client.handlers.transaction;

import com.neu.client.communication.CommunicationAPI;
import com.neu.client.communication.CommunicationAPIImpl;
import com.neu.client.sharableResource.SharableResource;
import com.neu.node.Node;
import com.neu.node.NodeChannel;
import com.neu.protocol.GeneralType;
import com.neu.protocol.joinAndLeaveProtocol.JoinAndLeaveProtocol;
import com.neu.protocol.joinAndLeaveProtocol.JoinAndLeaveType;
import com.neu.protocol.transactionProtocol.TransactionProtocol;
import com.neu.protocol.transactionProtocol.TransactionType;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketTimeoutException;

@Slf4j
public class TransactionImpl implements TransactionAPI {

    private final CommunicationAPI messageSender;

    public TransactionImpl() {
        this.messageSender = new CommunicationAPIImpl();
    };


    @Override
    public void prepare(Node nodeInfo, JoinAndLeaveType type) {
        TransactionProtocol message = new TransactionProtocol(GeneralType.TRANSACTION, type, TransactionType.PREPARE, nodeInfo);
        log.info("Broadcast the transaction prepare message: " + message);
        messageSender.broadcastExclude(message, nodeInfo.getId());
    }

    @Override
    public void accept(Channel channel) {
        TransactionProtocol res = new TransactionProtocol(GeneralType.TRANSACTION, TransactionType.ACCEPT);
        log.info("Responded to the transaction: " + res);
        channel.writeAndFlush(res);
    }

    @Override
    public void abort(Channel channel) {
        TransactionProtocol res = new TransactionProtocol(GeneralType.TRANSACTION, TransactionType.ABORT);
        log.info("Responded to the transaction: " + res);
        channel.writeAndFlush(res);
    }

    @Override
    public void commit(Node nodeInfo, JoinAndLeaveType type) {
        TransactionProtocol request = new TransactionProtocol(GeneralType.TRANSACTION, type, TransactionType.COMMIT, nodeInfo);
        log.info("Sent commit request to the transaction: " + request);
        messageSender.broadcastExclude(request, nodeInfo.getId());
    }

    @Override
    public void drop(Node nodeInfo, JoinAndLeaveType type) {
        TransactionProtocol request = new TransactionProtocol(GeneralType.TRANSACTION, type, TransactionType.DROP, nodeInfo);
        log.info("Sent drop request to the transaction: " + request);
        messageSender.broadcastExclude(request, nodeInfo.getId());
    }

    @Override
    public void ackCommit(Channel channel) {
        TransactionProtocol res = new TransactionProtocol(GeneralType.TRANSACTION, TransactionType.ACK_COMMIT);
        log.info("Responded an ACK message to the transaction: " + res);
        channel.writeAndFlush(res);
    }

    @Override
    public void ackDrop(Channel channel) {
        TransactionProtocol res = new TransactionProtocol(GeneralType.TRANSACTION, TransactionType.ACK_DROP);
        log.info("Responded an ACK message to the transaction: " + res);
        channel.writeAndFlush(res);
    }
}
