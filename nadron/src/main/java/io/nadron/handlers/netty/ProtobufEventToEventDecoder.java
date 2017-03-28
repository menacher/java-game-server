package io.nadron.handlers.netty;

import io.nadron.event.Event;
import io.nadron.event.Events;
import io.nadron.networking.NadronMessages.NadronEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by saurabhshukul on 14/02/17.
 */
public class ProtobufEventToEventDecoder extends MessageToMessageDecoder<NadronEvent> {

    private final Event event;

    private static final Logger LOG = LoggerFactory
            .getLogger(ProtobufEventToEventDecoder.class);

    /**
     * Creates a new instance.
     */
    public ProtobufEventToEventDecoder(Event event) {
        if (event == null) {
            throw new NullPointerException("event");
        }
        this.event = event;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, NadronEvent msg, List<Object> out) throws Exception {
        LOG.trace("Received message {}", msg);
        event.setType(msg.getEventType().getNumber());
        event.setSource(msg.getSource());
        event.setTimeStamp(msg.getTimeStamp());
        int opcode = msg.getEventType().getNumber();
        if (opcode == Events.NETWORK_MESSAGE)
        {
            opcode = Events.SESSION_MESSAGE;
        }
        event.setType(opcode);
        out.add(event);
    }
}

