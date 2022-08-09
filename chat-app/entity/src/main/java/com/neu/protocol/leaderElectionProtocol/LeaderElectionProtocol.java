package com.neu.protocol.leaderElectionProtocol;

import com.neu.node.Node;
import com.neu.protocol.GeneralType;
import com.neu.protocol.TransmitProtocol;

import lombok.Data;

import java.util.Objects;

@Data
public class LeaderElectionProtocol extends TransmitProtocol {

    private LeaderElectionType subType;

    private Node nodeInfo;

    private int performanceWeight;

    private String leaderToken;

    public LeaderElectionProtocol(GeneralType type, LeaderElectionType subType) {
        super(type);
        this.subType = subType;
    }

    public LeaderElectionProtocol(GeneralType type, LeaderElectionType subType, int performanceWeight) {
        super(type);
        this.subType = subType;
        this.performanceWeight = performanceWeight;
    }

    public LeaderElectionProtocol(GeneralType type, LeaderElectionType subType, String leaderToken) {
        super(type);
        this.subType = subType;
        this.leaderToken = leaderToken;
    }

    public LeaderElectionProtocol(GeneralType type, LeaderElectionType subType, Node nodeInfo) {
        super(type);
        this.subType = subType;
        this.nodeInfo = nodeInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LeaderElectionProtocol)) return false;
        if (!super.equals(o)) return false;
        LeaderElectionProtocol that = (LeaderElectionProtocol) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }
}
