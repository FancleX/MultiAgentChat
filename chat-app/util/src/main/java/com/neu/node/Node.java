package com.neu.node;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

/**
 * Node for storing metadata only. Distinct by the unique user id.
 * Extra information storage should modify the field or extend the class.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Node implements Comparable<Node>, Serializable {

    private static final long serialVersionUID = 1234567L;

    // the unique id of the user comes from database
    @NotNull
    protected Long id;

    // the nickname of the user
    protected String nickname;

    // is the leader node
    protected boolean isLeader;

    // the node hostname
    protected String hostname;

    // the node port
    protected int port;

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
