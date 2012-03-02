package org.menacheri.handlers.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.menacheri.communication.NettyMessageBuffer;
import org.menacheri.event.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Sharable
public class MessageBufferEventDecoder extends OneToOneDecoder
{
	private static final Logger LOG = LoggerFactory.getLogger(MessageBufferEventDecoder.class);
	
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception
	{
		if(null == msg)
		{
			LOG.error("Null message received in MessageBufferEventDecoder");
			return msg;
		}
		ChannelBuffer buffer = (ChannelBuffer)msg;
		byte opCode = buffer.readByte();
		return Events.event(new NettyMessageBuffer(buffer), opCode);
	}
}
