package com.neu.liveNodeList;


import io.netty.channel.Channel;

import java.util.*;

public class ServerLiveNodeListImpl<T extends Comparable<T>> implements LiveNodeList<T>, Iterable<T> {

    private final TreeSet<T> nodes;

    public ServerLiveNodeListImpl() {
        this.nodes = new TreeSet<>(T::compareTo);
    }


    @Override
    public Iterator<T> iterator() {
        return null;
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
}
