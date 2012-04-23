package org.menacheri.jetserver.handlers.netty;

import java.io.ByteArrayInputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Sharable
public class ByteArrayStreamDecoder extends OneToOneDecoder{
	private static final Logger LOG = LoggerFactory.getLogger(ByteArrayStreamDecoder.class);
	
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		if ((null == msg))
		{
			LOG.error("Incoming message is null");
			return msg;
		}
		Event event = (Event)msg;
		if(event.getType() == Events.SESSION_MESSAGE)
		{
			ChannelBuffer buffer = (ChannelBuffer)event.getSource();
			LOG.trace("BinaryArray with size:{} Received.", buffer.readableBytes());
			ByteArrayInputStream bis = new ByteArrayInputStream(buffer.array());
			event.setSource(bis);
		}
		return event;
	}

}
