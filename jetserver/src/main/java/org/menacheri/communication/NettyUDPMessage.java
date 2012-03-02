package org.menacheri.communication;

import java.net.SocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.menacheri.communication.IDeliveryGuaranty.DeliveryGuaranty;

public class NettyUDPMessage implements INettyMessage
{
	private ChannelBuffer buffer;
	private DatagramChannel channel;
	private SocketAddress remoteAddress;
	private static final IDeliveryGuaranty DELIVERY_GUARANTY = DeliveryGuaranty.FAST;
	
	public NettyUDPMessage()
	{
		
	}
	
	@Override
	public ChannelBuffer getMessage()
	{
		return buffer;
	}
	
	@Override
	public ChannelBuffer getChannelBuffer()
	{
		return buffer;
	}
	
	@Override
	public DatagramChannel getChannel()
	{
		return channel;
	}

	@Override
	public SocketAddress getSocketAddress()
	{
		return remoteAddress;
	}

	@Override
	public INettyMessage setChannel(Channel channel)
	{
		this.channel = (DatagramChannel)channel;
		return this;
		
	}

	@Override
	public INettyMessage setChannelBuffer(ChannelBuffer buffer)
	{
		this.buffer = buffer;
		return this;
	}

	@Override
	public INettyMessage setSocketAddress(SocketAddress remoteAddress)
	{
		this.remoteAddress = remoteAddress;
		return this;
	}

	@Override
	public IDeliveryGuaranty getDeliveryGuaranty()
	{
		return DELIVERY_GUARANTY;
	}

}
