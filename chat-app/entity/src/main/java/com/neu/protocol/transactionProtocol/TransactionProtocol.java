package com.neu.protocol.transactionProtocol;

import com.neu.node.Node;
import com.neu.protocol.GeneralType;
import com.neu.protocol.TransmitProtocol;
import com.neu.protocol.joinAndLeaveProtocol.JoinAndLeaveType;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransactionProtocol extends TransmitProtocol {

    private JoinAndLeaveType mainType;

    private TransactionType subType;

    private Node nodeInfo;

    public TransactionProtocol(GeneralType type, TransactionType subType) {
        super(type);
        this.subType = subType;
    }

    public TransactionProtocol(GeneralType type, JoinAndLeaveType mainType, TransactionType subType, Node nodeInfo) {
        super(type);
        this.mainType = mainType;
        this.subType = subType;
        this.nodeInfo = nodeInfo;
    }

    public TransactionProtocol(GeneralType type, JoinAndLeaveType mainType, Node nodeInfo) {
        super(type);
        this.mainType = mainType;
        this.nodeInfo = nodeInfo;
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
