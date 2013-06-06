package org.menacheri.jetserver.handlers.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.MessageBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import org.menacheri.jetserver.communication.NettyMessageBuffer;
import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.event.Events;

/**
 * This decoder will convert a Netty {@link ByteBuf} to a
 * {@link NettyMessageBuffer}. It will also convert
 * {@link Events#NETWORK_MESSAGE} events to {@link Events#SESSION_MESSAGE}
 * event.
 * 
 * @author Abraham Menacherry
 * 
 */
//TODO check if MessageToMessageDecoder can be replaced with MessageToByteDecoder
@Sharable
public class MessageBufferEventDecoder extends MessageToMessageDecoder<ByteBuf>
{

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer,
			MessageBuf<Object> out) throws Exception
	{
		out.add(decode(ctx, buffer));
	}
	
	public Event decode(ChannelHandlerContext ctx, ByteBuf buffer){
		byte opcode = buffer.readByte();
		if (opcode == Events.NETWORK_MESSAGE) 
		{
			opcode = Events.SESSION_MESSAGE;
		}
		ByteBuf data = Unpooled.buffer(buffer.readableBytes()).writeBytes(
				buffer);
		return Events.event(new NettyMessageBuffer(data), opcode);
	}
}
