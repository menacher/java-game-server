package io.nadron.handlers.netty;

import io.nadron.event.Events;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MessageList;
import io.netty.handler.codec.MessageToMessageDecoder;



@Sharable
public class EventDecoder extends MessageToMessageDecoder<ByteBuf>
{
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg,
			MessageList<Object> out) throws Exception
	{
		int opcode = msg.readUnsignedByte();
		if (Events.LOG_IN == opcode || Events.RECONNECT == opcode) 
		{
			msg.readUnsignedByte();// To read-destroy the protocol version byte.
		}
		ByteBuf buffer = Unpooled.buffer(msg.readableBytes()).writeBytes(msg);
		out.add(Events.event(buffer, opcode));
	}
	
}
