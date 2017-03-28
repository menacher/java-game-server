package io.nadron.handlers.netty;

import com.google.protobuf.Any;
import io.nadron.event.Event;
import io.nadron.networking.NadronMessages.EventType;
import io.nadron.networking.NadronMessages.NadronEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Created by saurabhshukul on 14/02/17.
 */
public class EventToProtobufEventEncoder extends MessageToMessageEncoder<Event> {

    private NadronEvent nadronEvent;

    /**
     * Creates a new instance.
     */
    public EventToProtobufEventEncoder(NadronEvent nadronEvent) {
        if (nadronEvent == null) {
            throw new NullPointerException("NadronEvent");
        }
        this.nadronEvent = nadronEvent;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Event msg, List<Object> out) throws Exception {

        nadronEvent = NadronEvent.newBuilder()
                .setEventType(EventType.forNumber(msg.getType()))
                .setTimeStamp(msg.getTimeStamp())
                .setSource((Any) msg.getSource())
                .build();

        out.add(nadronEvent);
    }
}

