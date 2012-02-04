package org.menacheri.handlers.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.menacheri.communication.IMessageBuffer;
import org.menacheri.event.IEvent;
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
		if (null == msg || !(msg instanceof IEvent))
		{
			LOG.error("Unexpected message: {} received in EventDecoder", msg);
			return msg;
		}
		IEvent event = (IEvent) msg;
		ChannelBuffer opCode = ChannelBuffers.buffer(1);
		opCode.writeByte(event.getType());
		ChannelBuffer buffer = null;
		if(null != event.getSource())
		{
			@SuppressWarnings("unchecked")
			IMessageBuffer<ChannelBuffer> msgBuffer = (IMessageBuffer<ChannelBuffer>)event.getSource();
			ChannelBuffer data = msgBuffer.getNativeBuffer();
			buffer = ChannelBuffers.wrappedBuffer(opCode, data);
		}
		else
		{
			buffer = opCode;
		}
		return buffer;
	}

}
