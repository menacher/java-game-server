package io.nadron.handlers.netty;

import io.nadron.communication.NettyMessageBuffer;
import io.nadron.event.Event;
import io.nadron.event.Events;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;


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
			List<Object> out) throws Exception
	{
		out.add(decode(ctx, buffer));
	}
	
	public Event decode(ChannelHandlerContext ctx, ByteBuf in){
		byte opcode = in.readByte();
		if (opcode == Events.NETWORK_MESSAGE) 
		{
			opcode = Events.SESSION_MESSAGE;
		}
		ByteBuf data = in.readBytes(in.readableBytes());
		return Events.event(new NettyMessageBuffer(data), opcode);
	}
}
