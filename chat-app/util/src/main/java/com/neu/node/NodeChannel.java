package com.neu.node;

import io.netty.channel.Channel;
import lombok.Data;

/**
 * A wrapper class of the io channel and stores metadata of the user
 */
@Data
public class NodeChannel extends Node {

    // the io channel of the user
    private Channel channel;
}
