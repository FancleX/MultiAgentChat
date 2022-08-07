package com.neu.node;

public class Node implements Comparable<Node> {

    // the unique id of the user comes from database
    private Long id;

    // the nickname of the user
    private String nickname;

    // is the leader node
    private boolean isLeader;

    // the node hostname
    private String hostname;

    // the node port
    private int port;

    @Override
    public int compareTo(Node o) {
        return 0;
    }
}
