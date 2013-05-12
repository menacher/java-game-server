package org.menacheri.jetserver.handlers.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import org.menacheri.jetserver.communication.MessageBuffer;
import org.menacheri.jetserver.event.Event;


@Sharable
public class MessageBufferEventEncoder extends MessageToMessageEncoder<Event>
{
	@Override
	protected Object encode(ChannelHandlerContext ctx, 
			Event event) throws Exception
	{
		ByteBuf out = null;
		if(null != event.getSource())
		{
			@SuppressWarnings("unchecked")
			MessageBuffer<ByteBuf> msgBuffer = (MessageBuffer<ByteBuf>)event.getSource();
			ByteBuf data = msgBuffer.getNativeBuffer();
			out = ctx.alloc().buffer(1 + data.readableBytes());
			out.writeByte(event.getType());
			out.writeBytes(data);
		}
		else
		{
			out = ctx.alloc().buffer(1);
			out.writeByte(event.getType());
		}
		return out;
	}

}
