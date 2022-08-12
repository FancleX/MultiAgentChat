package com.neu.node;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.ToString;


/**
 * A wrapper class of the io channel and stores metadata of the user.
 * For client use.
 * Extra information storage should modify the field or extend the class.
 */
@Data
@ToString
public class NodeChannel extends Node {

    // the io channel of the user
    private Channel channel;

    public NodeChannel(Node node, Channel channel) {
        super(node.getId(), node.getNickname(), node.isLeader(), node.getHostname(), node.getPort());
        this.channel = channel;
    }

    public NodeChannel(Long id, String nickname, boolean isLeader, String hostname, int port, Channel channel) {
        super(id, nickname, isLeader, hostname, port);
        this.channel = channel;
    }

    @Override
    public Node getNode() {
        return super.getNode();
    }

    @Override
    public int compareTo(Node o) {
        return super.compareTo(o);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
