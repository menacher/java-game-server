package org.menacheri.jetserver.handlers.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.menacheri.jetserver.event.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Sharable
public class EventDecoder extends OneToOneDecoder
{
	private static final Logger LOG = LoggerFactory.getLogger(EventDecoder.class);
	
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception
	{
		if(null == msg)
		{
			LOG.error("Null msg received in EventDecoder");
			return msg;
		}
		ChannelBuffer buffer = (ChannelBuffer)msg;
		int opcode = buffer.readUnsignedByte();
		if(Events.LOG_IN == opcode){
			buffer.readUnsignedByte();// To read-destroy the protocol version byte.
		}
		return Events.event(buffer, opcode);
	}
	
}
