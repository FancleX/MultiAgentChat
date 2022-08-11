package com.neu.client.ui;

import com.neu.client.communication.CommunicationAPI;
import com.neu.client.communication.CommunicationAPIImpl;
import com.neu.client.restClient.RestClient;
import com.neu.client.sharableResource.SharableResource;
import com.neu.node.Node;
import com.neu.protocol.GeneralType;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionProtocol;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionType;
import io.netty.channel.Channel;

import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * UI for user to interact. Should be located in a different thread.
 */
public class UI implements Runnable {

    private final CommunicationAPI writer;

    // for user signup and login
    private final RestClient restClient;

    public UI() {
        this.writer = new CommunicationAPIImpl();
        this.restClient = new RestClient();


        test();
    }

    public void test() {
        Map<String, Object> login = restClient.login("123@gmail.com", "123456", SharableResource.myHostname, SharableResource.myPort);
        Long id = Long.valueOf((Integer) login.get("id"));
        String nickname = (String) login.get("nickname");
        System.out.println("My id: " + id);
        System.out.println("My nickname: " + nickname);
        // parse hostname and port
        String hostname = (String) login.get("hostname");
        int port = (int) login.get("port");
        System.out.println("hostname: " + hostname);
        System.out.println("port: " + port);



        // if it is the sever info
        if (hostname.equals(SharableResource.serverHostname) && port == SharableResource.serverPort) {
            System.out.println("Current node is leader node");
            SharableResource.myNode = new Node(id, nickname, true, SharableResource.myHostname, SharableResource.myPort);
            try {
                SharableResource.server = SharableResource.group.connect(hostname, port);
                LeaderElectionProtocol leaderElectionProtocol = new LeaderElectionProtocol(GeneralType.LEADER_ELECTION, LeaderElectionType.CLIENT_REPORT, SharableResource.myNode);
                SharableResource.server.writeAndFlush(leaderElectionProtocol);
                SharableResource.server.close();
                System.out.println(SharableResource.server);
            } catch (SocketTimeoutException e) {
                throw new RuntimeException(e);
            }
        }

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
