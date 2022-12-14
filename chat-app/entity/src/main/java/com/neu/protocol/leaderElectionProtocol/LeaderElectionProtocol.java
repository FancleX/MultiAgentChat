package com.neu.protocol.leaderElectionProtocol;

import com.neu.node.Node;
import com.neu.protocol.GeneralType;
import com.neu.protocol.TransmitProtocol;

import lombok.Data;
import lombok.ToString;


@Data
@ToString
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

    public LeaderElectionProtocol(GeneralType type, LeaderElectionType subType, Node nodeInfo, String leaderToken) {
        super(type);
        this.subType = subType;
        this.nodeInfo = nodeInfo;
        this.leaderToken = leaderToken;
    }

    public LeaderElectionProtocol(GeneralType type, LeaderElectionType subType, Node nodeInfo) {
        super(type);
        this.subType = subType;
        this.nodeInfo = nodeInfo;
    }

    public LeaderElectionProtocol(GeneralType type, LeaderElectionType subType, Node nodeInfo, int performanceWeight) {
        super(type);
        this.subType = subType;
        this.nodeInfo = nodeInfo;
        this.performanceWeight = performanceWeight;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
