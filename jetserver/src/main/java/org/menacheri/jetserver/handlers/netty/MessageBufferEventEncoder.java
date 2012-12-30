package org.menacheri.jetserver.handlers.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.menacheri.jetserver.communication.MessageBuffer;
import org.menacheri.jetserver.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Sharable
public class MessageBufferEventEncoder extends OneToOneEncoder
{
	private static final Logger LOG = LoggerFactory
	.getLogger(MessageBufferEventEncoder.class);
	
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception
	{
		if (null == msg)
		{
			LOG.error("Null message received in MessageBufferEventEncoder");
			return msg;
		}
		Event event = (Event) msg;
		ChannelBuffer opcode = ChannelBuffers.buffer(1);
		opcode.writeByte(event.getType());
		ChannelBuffer buffer = null;
		if(null != event.getSource())
		{
			@SuppressWarnings("unchecked")
			MessageBuffer<ChannelBuffer> msgBuffer = (MessageBuffer<ChannelBuffer>)event.getSource();
			ChannelBuffer data = msgBuffer.getNativeBuffer();
			buffer = ChannelBuffers.wrappedBuffer(opcode, data);
		}
		else
		{
			buffer = opcode;
		}
		return buffer;
	}

}
