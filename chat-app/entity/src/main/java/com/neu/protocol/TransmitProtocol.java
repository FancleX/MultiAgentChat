package com.neu.protocol;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * The main protocol that will be transmitted over network.
 * Has a general type for a task that dispatchers can dispatch tasks to different handlers.
 * The sub protocols should extend the protocol.
 */
@Data
@NoArgsConstructor
@ToString
public class TransmitProtocol implements Serializable {

    private static final long serialVersionUID = 1234567L;

    protected GeneralType type;

    public TransmitProtocol(GeneralType type) {
        this.type = type;
    }
}
