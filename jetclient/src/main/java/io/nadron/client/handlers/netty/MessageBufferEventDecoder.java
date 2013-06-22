package io.nadron.client.handlers.netty;

import io.nadron.client.communication.NettyMessageBuffer;
import io.nadron.client.event.Event;
import io.nadron.client.event.Events;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MessageList;
import io.netty.handler.codec.ByteToMessageDecoder;


/**
 * This decoder will convert a Netty {@link ByteBuf} to a
 * {@link NettyMessageBuffer}. It will also convert
 * {@link Events#NETWORK_MESSAGE} events to {@link Events#SESSION_MESSAGE}
 * event.
 * 
 * @author Abraham Menacherry
 * 
 */
@Sharable
public class MessageBufferEventDecoder extends ByteToMessageDecoder
{
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			MessageList<Object> out) throws Exception
	{
		if (in.readableBytes() > 0) 
		{
			out.add(decode(ctx, in));
		}
	}
	
	protected Event decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception
	{
		if (in.readableBytes() > 0) 
		{
			byte opcode = in.readByte();
			if (opcode == Events.NETWORK_MESSAGE) 
			{
				opcode = Events.SESSION_MESSAGE;
			}
			ByteBuf data = Unpooled.buffer(in.readableBytes()).writeBytes(in);
			return Events.event(new NettyMessageBuffer(data), opcode);
		}
		return null;
	}
	
}
