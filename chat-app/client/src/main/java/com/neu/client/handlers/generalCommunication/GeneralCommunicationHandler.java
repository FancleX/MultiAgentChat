package com.neu.client.handlers.generalCommunication;

import com.neu.handlerAPI.GeneralEventHandlerAPI;
import com.neu.protocol.generalCommunicationProtocol.GeneralCommunicationProtocol;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneralCommunicationHandler implements GeneralEventHandlerAPI<GeneralCommunicationProtocol> {


    public GeneralCommunicationHandler() {}

    @Override
    public void handle(GeneralCommunicationProtocol protocol, ChannelHandlerContext ctx) {
        switch (protocol.getSubType()) {
            case PRIVATE_MESSAGE:

                break;
            case BROADCAST_MESSAGE:
                break;
        }

    }

    /**
     * Format the raw string to a stander response format.
     *
     * @param rawString the raw string
     * @return the formatted string
     */
    private String formatter(String rawString) {
        return null;
    }
}
