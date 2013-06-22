package io.nadron.client.handlers.netty;

import io.nadron.client.event.Event;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MessageList;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;


public class UDPEventEncoder extends MessageBufferEventEncoder 
{
	private InetSocketAddress udpServerAddress;
	
	public UDPEventEncoder(InetSocketAddress udpServerAddress)
	{
		this.udpServerAddress = udpServerAddress;
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Event event,
			MessageList<Object> out) throws Exception
	{
		ByteBuf data = (ByteBuf)super.encode(ctx, event);
		out.add(new DatagramPacket(data, udpServerAddress));
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
