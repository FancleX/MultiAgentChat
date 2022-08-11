package com.neu.client.sharableResource;

import com.neu.client.dispatcher.ClientTaskDispatcher;
import com.neu.liveNodeList.ClientLiveNodeListImpl;
import com.neu.liveNodeList.LiveNodeList;
import com.neu.node.Node;
import com.neu.node.NodeChannel;
import com.neu.p2pConnectionGroup.P2PConnectionGroup;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Value;

/**
 * Static resource shared with all system modules.
 */
public final class SharableResource {

    // server part
    public static Channel server;

    public static String serverHostname;

    public static int serverPort;

    /**
     * The url mapping of the rest api
     */
    public static String baseURL;

    // node part
    public static LiveNodeList<NodeChannel> liveNodeList;

    public static P2PConnectionGroup group;

    // to determine if my node is the leader
    public static String leaderNodeToken;

    // node create after user login
    public static Node myNode;

    public static String myHostname;

    public static int myPort;

    public static void init(String hostname, int port) {
        myHostname = hostname;
        myPort = port;
        liveNodeList = new ClientLiveNodeListImpl<>();
        group = new P2PConnectionGroup(port, new ClientTaskDispatcher());
    }

}
