package com.neu.liveNodeList;

import com.neu.node.Node;
import io.netty.channel.Channel;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class ClientLiveNodeListImpl<T extends Comparable<T>> implements LiveNodeList<T>, Iterable<T> {

    private final Map<T, Channel> nodes;

    public ClientLiveNodeListImpl() {
        this.nodes = new TreeMap<>(T::compareTo);
    }

    @Override
    public boolean add(T node) {
        return false;
    }

    @Override
    public boolean remove(Long id) {
        return false;
    }

    @Override
    public boolean isContain(Long id) {
        return false;
    }


    @Override
    public T get(Long id) {
        return null;
    }

    @Override
    public T getLeaderNode() {
        return null;
    }

    @Override
    public Iterable<T> getAllNodes() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }
}
