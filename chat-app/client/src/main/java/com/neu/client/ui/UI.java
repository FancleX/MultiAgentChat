package com.neu.client.ui;

import com.neu.client.communication.CommunicationAPI;
import com.neu.client.communication.CommunicationAPIImpl;
import com.neu.client.restClient.RestClient;
import com.neu.liveNodeList.LiveNodeList;
import com.neu.node.NodeChannel;

/**
 * UI for user to interact. Should be located in a different thread.
 */
public class UI implements Runnable {

    // display current online user
    private final LiveNodeList<NodeChannel> liveNodeList;

    private final CommunicationAPI writer;

    // for user signup and login
    private final RestClient restClient;

    public UI(LiveNodeList<NodeChannel> liveNodeList) {
        this.liveNodeList = liveNodeList;
        this.writer = new CommunicationAPIImpl(liveNodeList);
        this.restClient = new RestClient();
    }

    /**
     * For user exit the program, this should shut down the whole system.
     */
    public void onExit() {
        System.exit(0);
    }

    @Override
    public void run() {

    }
}
