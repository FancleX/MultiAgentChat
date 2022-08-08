package com.neu.node;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Node implements Comparable<Node>, Serializable {

    // the unique id of the user comes from database
    @NotNull
    private Long id;

    // the nickname of the user
    private String nickname;

    // is the leader node
    private boolean isLeader;

    // the node hostname
    private String hostname;

    // the node port
    private int port;

    public Node getNode() {
        return this;
    }

    @Override
    public int compareTo(@NotNull Node o) {
        return id.compareTo(o.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return id.equals(node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
