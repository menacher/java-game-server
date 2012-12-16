package org.menacheri.jetclient.handlers.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.menacheri.jetclient.communication.MessageBuffer;
import org.menacheri.jetclient.event.Event;
import org.menacheri.jetclient.event.Events;

/**
 * Converts an incoming {@link Event} which in turn has a
 * IMessageBuffer<ChannelBuffer> payload to a Netty {@link ChannelBuffer}.
 * <b>Note that {@link Event} instances containing other type of objects as its
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
		Event event = (Event) msg;
		ChannelBuffer opCode = ChannelBuffers.buffer(1);
		opCode.writeByte(event.getType());
		if (Events.LOG_IN == event.getType())
		{
			// write protocol version also
			ChannelBuffer protocolVersion = ChannelBuffers.buffer(1);
			protocolVersion.writeByte(Events.PROTOCOL_VERSION);
			opCode = ChannelBuffers.wrappedBuffer(opCode, protocolVersion);
		}
		
		ChannelBuffer buffer = null;
		if (null != event.getSource())
		{
			@SuppressWarnings("unchecked")
			MessageBuffer<ChannelBuffer> msgBuffer = (MessageBuffer<ChannelBuffer>) event
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
