package com.neu.client.channel;

import lombok.Data;

/**
 * A wrapper class of the io channel and stores metadata of the user
 */
@Data
public class NodeChannel implements Comparable<NodeChannel> {

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

//    // the io channel of the user
//    private Channel channel;

    @Override
    public int compareTo(NodeChannel o) {
        return id.compareTo(o.id);
    }
}
