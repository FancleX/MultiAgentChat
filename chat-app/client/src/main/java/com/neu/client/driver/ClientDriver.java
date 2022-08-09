package com.neu.client.driver;

import com.neu.client.dispatcher.ClientTaskDispatcher;
import com.neu.client.ui.UI;
import com.neu.liveNodeList.ClientLiveNodeListImpl;
import com.neu.liveNodeList.LiveNodeList;
import com.neu.node.NodeChannel;
import com.neu.p2pConnectionGroup.P2PConnectionGroup;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.net.SocketTimeoutException;

public class ClientDriver {

    // share the properties with the ui part and others
    private static P2PConnectionGroup group;

    public static void start(int port) {
        ClientLiveNodeListImpl<NodeChannel> liveNodeList = new ClientLiveNodeListImpl<>();
        group = new P2PConnectionGroup(port, new ClientTaskDispatcher(liveNodeList));
        new Thread(new UI(liveNodeList)).start();
    }

    public static P2PConnectionGroup getGroup() {
        return group;
    }

//    public ClientDriver(int port) {
//        this.liveNodeList = new ClientLiveNodeListImpl<>();
//        group = new P2PConnectionGroup(port, new ClientTaskDispatcher(this.liveNodeList));
//
//        this.ui = new UI(liveNodeList);
//        new Thread(ui).start();
////        try {
////            Channel channel = group.connect("localhost", 9000);
////            channel.writeAndFlush(new A("abc"));
////        } catch (SocketTimeoutException e) {
////            throw new RuntimeException(e);
////        }
//    }

//    public static void main(String[] args) throws SocketTimeoutException {
//        P2PConnectionGroup group = new P2PConnectionGroup(Integer.parseInt(args[0]), new ClientTaskDispatcher());
//        Channel channel = group.connect("localhost", 9001);
//        channel.writeAndFlush(new TransmitProtocol());
//        channel.writeAndFlush(new TransmitProtocol());
//        channel.writeAndFlush(new TransmitProtocol());
//    }

}
