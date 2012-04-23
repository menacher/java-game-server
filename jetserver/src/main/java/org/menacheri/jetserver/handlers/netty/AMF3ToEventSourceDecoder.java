package org.menacheri.jetserver.handlers.netty;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.menacheri.jetserver.event.Event;

/**
 * If the incoming event is of type {@link Event} then it will only
 * de-serialize the source of the event rather than the whole event object. The
 * de-serialized source is now set as source of the event.
 * 
 * @author Abraham Menacherry
 * 
 */
public class AMF3ToEventSourceDecoder extends AMF3ToJavaObjectDecoder
{
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception
	{
		Event event = (Event) msg;
		Object source = super.decode(ctx, channel, event.getSource());
		event.setSource(source);
		return event;
	}
}
