package org.menacheri.jetclient.handlers.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import org.menacheri.jetclient.communication.MessageBuffer;
import org.menacheri.jetclient.event.Event;
import org.menacheri.jetclient.event.Events;

/**
 * Converts an incoming {@link Event} which in turn has a
 * IMessageBuffer<ByteBuf> payload to a Netty {@link ByteBuf}.
 * <b>Note that {@link Event} instances containing other type of objects as its
 * payload will result in {@link ClassCastException}.
 * 
 * @author Abraham Menacherry.
 * 
 */
// TODO check if MessageToMessageEncoder can be replaced with MessageToByteEncoder
@Sharable
public class MessageBufferEventEncoder extends MessageToMessageEncoder<Event>
{

	@Override
	protected Object encode(ChannelHandlerContext ctx, Event event)
			throws Exception {
		ByteBuf out = ctx.alloc().buffer();
		out.writeByte(event.getType());
		if (Events.LOG_IN == event.getType() || Events.RECONNECT == event.getType())
		{
			// write protocol version also
			out.writeByte(Events.PROTOCOL_VERSION);
		}
		
		if (null != event.getSource())
		{
			@SuppressWarnings("unchecked")
			MessageBuffer<ByteBuf> msgBuffer = (MessageBuffer<ByteBuf>) event
					.getSource();
			ByteBuf data = msgBuffer.getNativeBuffer();
			out.writeBytes(data);
		}
		return out;
	}

}
