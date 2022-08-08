package com.neu.client.driver;

import com.neu.client.dispatcher.ClientTaskDispatcher;
import com.neu.client.ui.UI;
import com.neu.liveNodeList.ClientLiveNodeListImpl;
import com.neu.liveNodeList.LiveNodeList;
import com.neu.node.NodeChannel;
import com.neu.p2pConnectionGroup.P2PConnectionGroup;

public class ClientDriver {

    // share the properties with the ui part and others
    private final P2PConnectionGroup group;
    private final LiveNodeList<NodeChannel> liveNodeList;

    private final UI ui;


    public ClientDriver(int port) {
        this.liveNodeList = new ClientLiveNodeListImpl<>();
        this.group = new P2PConnectionGroup(port, new ClientTaskDispatcher(this.liveNodeList));
        this.ui = new UI(liveNodeList);
        new Thread(ui).start();
    }

//    public static void main(String[] args) throws SocketTimeoutException {
//        P2PConnectionGroup group = new P2PConnectionGroup(Integer.parseInt(args[0]), new ClientTaskDispatcher());
//        Channel channel = group.connect("localhost", 9001);
//        channel.writeAndFlush(new TransmitProtocol());
//        channel.writeAndFlush(new TransmitProtocol());
//        channel.writeAndFlush(new TransmitProtocol());
//    }

}
