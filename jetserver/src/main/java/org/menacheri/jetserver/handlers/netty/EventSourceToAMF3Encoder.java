package org.menacheri.jetserver.handlers.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.Event;

@Sharable
public class EventSourceToAMF3Encoder extends JavaObjectToAMF3Encoder
{
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception
	{
		Event event = (Event)msg;
		ChannelBuffer payload = (ChannelBuffer) super.encode(ctx, channel, event.getSource());
		return Events.event(payload, event.getType());
	}
}
