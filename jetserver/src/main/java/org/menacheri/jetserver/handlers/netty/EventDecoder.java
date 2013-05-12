package org.menacheri.jetserver.handlers.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import org.menacheri.jetserver.event.Events;


@Sharable
public class EventDecoder extends MessageToMessageDecoder<ByteBuf>
{
	@Override
	protected Object decode(ChannelHandlerContext ctx,
			ByteBuf msg) throws Exception {
		int opcode = msg.readUnsignedByte();
		if (Events.LOG_IN == opcode || Events.RECONNECT == opcode) 
		{
			msg.readUnsignedByte();// To read-destroy the protocol version byte.
		}
		ByteBuf buffer = Unpooled.buffer(msg.readableBytes()).writeBytes(msg);
		return Events.event(buffer, opcode);
	}
	
}
