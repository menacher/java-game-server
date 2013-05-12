package org.menacheri.jetclient.handlers.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import org.menacheri.jetclient.communication.NettyMessageBuffer;
import org.menacheri.jetclient.event.Events;

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
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception 
	{
		if (in.readableBytes() > 0) 
		{
			byte opcode = in.readByte();
			if (opcode == Events.NETWORK_MESSAGE) 
			{
				opcode = Events.SESSION_MESSAGE;
			}
			return Events.event(new NettyMessageBuffer(in), opcode);
		} 
		else 
		{
			return null;
		}
	}
}
