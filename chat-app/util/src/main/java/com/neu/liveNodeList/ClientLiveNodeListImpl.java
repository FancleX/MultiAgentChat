package com.neu.liveNodeList;

import com.neu.node.Node;
import com.neu.node.NodeChannel;
import io.netty.channel.Channel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * List for client use.
 *
 * @param <T> metadata node + io channel
 */
public class ClientLiveNodeListImpl<T extends NodeChannel> implements LiveNodeList<T>, Iterable<T> {

    private final TreeSet<T> nodes;

    public ClientLiveNodeListImpl() {
        this.nodes = new TreeSet<>(T::compareTo);
    }

    @Override
    public synchronized boolean add(T node) {
        if (node == null) {
            return false;
        }
        return nodes.add(node);
    }

    @Override
    public synchronized boolean remove(Long id) {
        if (id == null) {
            return false;
        }
        T t = get(id);
        if (t == null) {
            return false;
        }
        Channel channelToBeRemoved = t.getChannel();
        nodes.remove(t);
        if (channelToBeRemoved != null) {
            // close the channel with all handler resources associated with the channel
            channelToBeRemoved.close();
        }
        return true;
    }

    @Override
    public synchronized boolean isContain(Long id) {
        if (id == null) {
            return false;
        }
        T t = get(id);
        return t != null;
    }


    @Override
    public synchronized T get(Long id) {
        if (id == null) {
            return null;
        }
        List<T> collect = nodes.stream().filter(node -> node.getId().equals(id)).collect(Collectors.toList());
        return collect.isEmpty() ? null : collect.get(0);
    }

    @Override
    public synchronized T getLeaderNode() {
        if (nodes.isEmpty()) {
            return null;
        }
        List<T> collect = nodes.stream().filter(Node::isLeader).collect(Collectors.toList());
        return collect.isEmpty() ? null : collect.get(0);
    }

    @Override
    public synchronized Iterator<T> getAllNodes() {
        return iterator();
    }

    @Override
    public synchronized int size() {
        return nodes.size();
    }

    @Override
    public synchronized T getNext() {
        if (nodes.size() == 0) {
            return null;
        }
        List<T> collect = nodes.stream().filter(node -> !node.isLeader()).collect(Collectors.toList());
        return collect.isEmpty() ? null : collect.get(0);
    }

    @Override
    public synchronized Iterator<T> iterator() {
        return nodes.iterator();
    }
}
