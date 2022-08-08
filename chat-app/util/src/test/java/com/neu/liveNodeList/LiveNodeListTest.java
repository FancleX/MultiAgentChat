package com.neu.liveNodeList;

import com.neu.node.Node;
import com.neu.node.NodeChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LiveNodeListTest {

    private LiveNodeList<Node> serverList;

    private LiveNodeList<NodeChannel> clientList;

    @BeforeEach
    void setUp() {
        this.serverList = new ServerLiveNodeListImpl<>();
        this.clientList = new ClientLiveNodeListImpl<>();
    }

    @Test
    void add() {
        serverList.add(null);
        assertSame(true, serverList.add(new Node(1L, "abc", false, "localhost", 9000)));
        assertSame(false, serverList.add(new Node(1L, "abc", false, "localhost", 9000)));


        clientList.add(null);
        assertSame(true, clientList.add(new NodeChannel(1L, "abc", false, "localhost", 9000, null)));
        assertSame(false, clientList.add(new NodeChannel(1L, "abc", false, "localhost", 9000, null)));
    }

    @Test
    void remove() {
        assertSame(false, serverList.remove(null));
        assertSame(false, serverList.remove(9999L));
        serverList.add(new Node(1L, "abc", false, "localhost", 9000));
        serverList.remove(1L);
        assertSame(0, serverList.size());

        assertSame(false, clientList.remove(null));
        assertSame(false, clientList.remove(9999L));
        clientList.add(new NodeChannel(1L, "abc", false, "localhost", 9000, null));
        clientList.remove(1L);
        assertSame(0, clientList.size());
    }

    @Test
    void isContain() {
        assertSame(false, serverList.isContain(null));
        serverList.add(new Node(1L, "abc", false, "localhost", 9000));
        assertSame(false, serverList.isContain(2L));
        assertSame(true, serverList.isContain(1L));

        assertSame(false, clientList.isContain(null));
        clientList.add(new NodeChannel(1L, "abc", false, "localhost", 9000, null));
        assertSame(false, clientList.isContain(2L));
        assertSame(true, clientList.isContain(1L));
    }

    @Test
    void get() {
        assertSame(null, serverList.get(1L));
        Node node = new Node(1L, "abc", false, "localhost", 9000);
        serverList.add(node);
        assertSame(true, node.equals(serverList.get(1L)));
        assertSame(false, node.equals(serverList.get(2L)));

        assertSame(null, clientList.get(1L));
        NodeChannel node1 = new NodeChannel(1L, "abc", false, "localhost", 9000, null);
        clientList.add(node1);
        assertSame(true, node.equals(clientList.get(1L)));
        assertSame(false, node.equals(clientList.get(2L)));
    }

    @Test
    void getLeaderNode() {
        assertSame(null, serverList.getLeaderNode());
        Node node = new Node(1L, "abc", true, "localhost", 9000);
        Node node1 = new Node(2L, "abc", false, "localhost", 9000);
        serverList.add(node);
        serverList.add(node1);
        assertSame(true, node.equals(serverList.getLeaderNode()));

        assertSame(null, clientList.getLeaderNode());
        NodeChannel node2 = new NodeChannel(1L, "abc", true, "localhost", 9000, null);
        NodeChannel node3 = new NodeChannel(2L, "abc", false, "localhost", 9000, null);
        serverList.add(node);
        serverList.add(node1);
        assertSame(true, node2.equals(serverList.getLeaderNode()));
    }

    @Test
    void getAllNodes() {
        Node node = new Node(1L, "abc", false, "localhost", 9000);
        Node node1 = new Node(2L, "abc", false, "localhost", 9000);
        Node node2 = new Node(3L, "abc", false, "localhost", 9000);
        List<Node> nodes = new ArrayList<>();
        nodes.add(node);
        nodes.add(node1);
        nodes.add(node2);
        serverList.add(node);
        serverList.add(node1);
        serverList.add(node2);
        Iterator<Node> allNodes = serverList.getAllNodes();
        int i = 0;
        while (allNodes.hasNext()) {
            assertSame(true, allNodes.next().equals(nodes.get(i)));
            i++;
        }

        NodeChannel node3 = new NodeChannel(1L, "abc", false, "localhost", 9000, null);
        NodeChannel node4 = new NodeChannel(2L, "abc", false, "localhost", 9000, null);
        NodeChannel node5 = new NodeChannel(3L, "abc", false, "localhost", 9000, null);
        List<NodeChannel> nodes1 = new ArrayList<>();
        nodes1.add(node3);
        nodes1.add(node4);
        nodes1.add(node5);
        clientList.add(node3);
        clientList.add(node4);
        clientList.add(node5);
        Iterator<NodeChannel> allNodes1 = clientList.getAllNodes();
        int j = 0;
        while (allNodes1.hasNext()) {
            assertSame(true, allNodes1.next().equals(nodes1.get(j)));
            j++;
        }
    }

    @Test
    void size() {
        serverList.add(null);
        serverList.add(new Node(1L, "abc", false, "localhost", 9000));
        serverList.add(new Node(1L, "abc", false, "localhost", 9000));
        assertSame(1, serverList.size());

        clientList.add(null);
        clientList.add(new NodeChannel(1L, "abc", false, "localhost", 9000, null));
        clientList.add(new NodeChannel(1L, "abc", false, "localhost", 9000, null));
        assertSame(1, clientList.size());
    }

    @Test
    void getNext() {
        assertSame(null, serverList.getNext());
        Node node = new Node(1L, "abc", true, "localhost", 9000);
        Node node1 = new Node(2L, "abc", false, "localhost", 9000);
        Node node2 = new Node(3L, "abc", false, "localhost", 9000);
        serverList.add(node);
        serverList.add(node1);
        serverList.add(node2);
        assertSame(true, node1.equals(serverList.getNext()));
        assertSame(false, node2.equals(serverList.getNext()));


        assertSame(null, clientList.getNext());
        NodeChannel node3 = new NodeChannel(1L, "abc", true, "localhost", 9000, null);
        NodeChannel node4 = new NodeChannel(2L, "abc", false, "localhost", 9000, null);
        NodeChannel node5 = new NodeChannel(3L, "abc", false, "localhost", 9000, null);
        clientList.add(node3);
        clientList.add(node4);
        clientList.add(node5);
        assertSame(true, node4.equals(clientList.getNext()));
        assertSame(false, node5.equals(clientList.getNext()));
    }
}