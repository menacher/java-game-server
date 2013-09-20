package io.nadron.client.handlers.netty;

import io.nadron.client.event.Event;
import io.nadron.client.event.Events;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.Serializable;

public class EventObjectEncoder extends MessageToByteEncoder<Event>{

	@Override
	protected void encode(ChannelHandlerContext ctx, Event event, ByteBuf out)
			throws Exception {
		out.writeByte(event.getType());
		if (Events.LOG_IN == event.getType() || Events.RECONNECT == event.getType())
		{
			// write protocol version also
			out.writeByte(Events.PROTOCOL_VERSION);
		}
		
		if (null != event.getSource())
		{
			new SourceEncoder().encode(ctx, (Serializable)event.getSource(), out);
		}
	}
	
	public static class SourceEncoder extends ObjectEncoder
	{
		@Override
		protected void encode(ChannelHandlerContext ctx, Serializable msg,
				ByteBuf out) throws Exception {
			super.encode(ctx, msg, out);
		}
	}

}
