package org.menacheri.jetclient.handlers.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.menacheri.jetclient.communication.IMessageBuffer;
import org.menacheri.jetclient.event.IEvent;

/**
 * Converts an incoming {@link IEvent} which in turn has a
 * IMessageBuffer<ChannelBuffer> payload to a Netty {@link ChannelBuffer}.
 * <b>Note that {@link IEvent} instances containing other type of objects as its
 * payload will result in {@link ClassCastException}.
 * 
 * @author Abraham Menacherry.
 * 
 */
@Sharable
public class MessageBufferEventEncoder extends OneToOneEncoder
{

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception
	{
		if (null == msg)
		{
			return msg;
		}
		IEvent event = (IEvent) msg;
		ChannelBuffer opCode = ChannelBuffers.buffer(1);
		opCode.writeByte(event.getType());
		ChannelBuffer buffer = null;
		if (null != event.getSource())
		{
			@SuppressWarnings("unchecked")
			IMessageBuffer<ChannelBuffer> msgBuffer = (IMessageBuffer<ChannelBuffer>) event
					.getSource();
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
