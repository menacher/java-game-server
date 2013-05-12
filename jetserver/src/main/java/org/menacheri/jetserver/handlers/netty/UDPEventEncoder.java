package org.menacheri.jetserver.handlers.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

import org.menacheri.jetserver.event.Event;

@Sharable
public class UDPEventEncoder extends MessageBufferEventEncoder 
{
	@Override
	protected Object encode(ChannelHandlerContext ctx, Event msg)
			throws Exception 
	{
		ByteBuf data = (ByteBuf) super.encode(ctx, msg);
		InetSocketAddress clientAddress = (InetSocketAddress) msg
				.getEventContext().getAttachment();
		return new DatagramPacket(data, clientAddress);
	}
	
}
