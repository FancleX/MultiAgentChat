package com.neu.liveNodeList;


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
}
