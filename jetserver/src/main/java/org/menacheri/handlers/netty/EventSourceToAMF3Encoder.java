package org.menacheri.handlers.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.menacheri.event.IEvent;

public class EventSourceToAMF3Encoder extends JavaObjectToAMF3Encoder
{
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception
	{
		IEvent event = (IEvent)msg;
		ChannelBuffer payload = (ChannelBuffer) super.encode(ctx, channel, event.getSource());
		event.setSource(payload);
		return event;
	}
}
