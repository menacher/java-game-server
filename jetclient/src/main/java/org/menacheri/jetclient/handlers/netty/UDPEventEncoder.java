package org.menacheri.jetclient.handlers.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

import org.menacheri.jetclient.event.Event;

public class UDPEventEncoder extends MessageBufferEventEncoder 
{
	private InetSocketAddress udpServerAddress;
	
	public UDPEventEncoder(InetSocketAddress udpServerAddress)
	{
		this.udpServerAddress = udpServerAddress;
	}
	
	@Override
	protected Object encode(ChannelHandlerContext ctx, Event event)
			throws Exception 
	{
		ByteBuf data = (ByteBuf)super.encode(ctx, event);
		return new DatagramPacket(data, udpServerAddress);
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
