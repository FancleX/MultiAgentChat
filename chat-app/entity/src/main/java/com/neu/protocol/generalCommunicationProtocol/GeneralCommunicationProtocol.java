package com.neu.protocol.generalCommunicationProtocol;

import com.neu.protocol.GeneralType;
import com.neu.protocol.TransmitProtocol;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class GeneralCommunicationProtocol extends TransmitProtocol {

    private GeneralCommunicationType subType;

    private Long sender;

    private Long receiver;
    private String messageContent;

    public GeneralCommunicationProtocol(GeneralType type, GeneralCommunicationType subType, String messageContent) {
        super(type);
        this.subType = subType;
        this.messageContent = messageContent;
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
