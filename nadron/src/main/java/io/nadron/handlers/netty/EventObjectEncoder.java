package io.nadron.handlers.netty;

import io.nadron.event.Event;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.Serializable;
import java.util.List;

public class EventObjectEncoder extends MessageToMessageEncoder<Event> 
{
	@Override
	protected void encode(ChannelHandlerContext ctx, Event event, List<Object> out)
			throws Exception {
		ByteBuf data = ctx.alloc().buffer();
		data.writeByte(event.getType());
		if (null != event.getSource())
		{
			new SourceEncoder().encode(ctx, (Serializable)event.getSource(), data);
		}
		out.add(data);
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
