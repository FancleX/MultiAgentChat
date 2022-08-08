package com.neu.liveNodeList;


import com.neu.node.Node;

import java.util.*;
import java.util.stream.Collectors;

public class ServerLiveNodeListImpl<T extends Node> implements LiveNodeList<T>, Iterable<T> {

    private final TreeSet<T> nodes;

    public ServerLiveNodeListImpl() {
        this.nodes = new TreeSet<>(T::compareTo);
    }


    @Override
    public Iterator<T> iterator() {
        return nodes.iterator();
    }

    @Override
    public boolean add(T node) {
        if (node == null) {
            return false;
        }
        return nodes.add(node);
    }

    @Override
    public boolean remove(Long id) {
        if (id == null) {
            return false;
        }
        // query the node
        T t = get(id);
        if (t == null) {
            return false;
        }
        nodes.remove(t);
        return true;
    }

    @Override
    public boolean isContain(Long id) {
        if (id == null) {
            return false;
        }
        T t = get(id);
        return t != null;
    }

    @Override
    public T get(Long id) {
        if (id == null) {
            return null;
        }
        List<T> collect = nodes.stream().filter(node -> node.getId().equals(id)).collect(Collectors.toList());
        return collect.isEmpty() ? null : collect.get(0);
    }

    @Override
    public T getLeaderNode() {
        if (nodes.isEmpty()) {
            return null;
        }
        List<T> collect = nodes.stream().filter(Node::isLeader).collect(Collectors.toList());
        return collect.isEmpty() ? null : collect.get(0);
    }

    @Override
    public Iterator<T> getAllNodes() {
        return iterator();
    }

    @Override
    public int size() {
        return nodes.size();
    }

    /**
     * Get the first none leader node in the list.
     *
     * @return a node. If the list only contains the leader node, this method will return null.
     */
    public T getNext() {
        if (nodes.size() == 0) {
            return null;
        }
        List<T> collect = nodes.stream().filter(node -> !node.isLeader()).collect(Collectors.toList());
        return collect.isEmpty() ? null : collect.get(0);
    }
}
