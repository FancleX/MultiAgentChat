package com.neu.liveNodeList;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.TreeMap;

public class ClientLiveNodeListImpl<T extends Comparable<T>> implements LiveNodeList<T>, Iterable<T> {

    private final Map<T, Channel> nodes;

    public ClientLiveNodeListImpl() {
        this.nodes = new TreeMap<>(T::compareTo);
    }
}
