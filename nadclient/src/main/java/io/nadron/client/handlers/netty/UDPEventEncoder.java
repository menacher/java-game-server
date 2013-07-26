package io.nadron.client.handlers.netty;

import io.nadron.client.event.Event;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.util.List;


@Sharable
public class UDPEventEncoder extends MessageBufferEventEncoder 
{
	private InetSocketAddress udpServerAddress;
	
	public UDPEventEncoder(InetSocketAddress udpServerAddress)
	{
		this.udpServerAddress = udpServerAddress;
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Event event,
			List<Object> out) throws Exception
	{
		ByteBuf data = (ByteBuf)super.encode(ctx, event);
		out.add(new DatagramPacket(data, udpServerAddress));
		ctx.flush();
	}
	
	public InetSocketAddress getUdpServerAddress() 
	{
		return udpServerAddress;
	}
	public void setUdpServerAddress(InetSocketAddress udpServerAddress) 
	{
		this.udpServerAddress = udpServerAddress;
	}
	
}
