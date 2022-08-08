package com.neu.protocol;


import lombok.Data;

import java.io.Serializable;

/**
 * The main protocol that will be transmitted over network.
 * Has a general type for a task that dispatchers can dispatch tasks to different handlers.
 * The sub protocols should extend the protocol.
 */
@Data
public class TransmitProtocol implements Serializable {

    private static final long serialVersionUID = 1234567L;

    private GeneralType type;

}
