package com.neu.client.driver;

import com.neu.client.dispatcher.ClientTaskDispatcher;
import com.neu.p2pConnectionGroup.P2PConnectionGroup;
import com.neu.protocol.TransmitProtocol;
import io.netty.channel.Channel;

import java.net.SocketTimeoutException;

public class ClientDriver {

    public ClientDriver(int port) {
        P2PConnectionGroup group = new P2PConnectionGroup(port, new ClientTaskDispatcher());

    }

//    public static void main(String[] args) throws SocketTimeoutException {
//        P2PConnectionGroup group = new P2PConnectionGroup(Integer.parseInt(args[0]), new ClientTaskDispatcher());
//        Channel channel = group.connect("localhost", 9001);
//        channel.writeAndFlush(new TransmitProtocol());
//        channel.writeAndFlush(new TransmitProtocol());
//        channel.writeAndFlush(new TransmitProtocol());
//    }

}
