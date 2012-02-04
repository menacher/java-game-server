package org.menacheri.communication;

import java.net.SocketAddress;

import org.jboss.netty.channel.socket.DatagramChannel;
import org.menacheri.app.ISession;
import org.menacheri.event.Events;


/**
 * This class is used to send messages to a remote UDP client or server. An
 * instance of this class will be created when the {@link Events#CONNECT_UDP}
 * event is sent to a {@link ISession}
 * 
 * @author Abraham Menacherry
 * 
 */
public class NettyUDPMessageSender implements IMessageSender
{
	private SocketAddress remoteAddress;
	private DatagramChannel channel;
	private static final IDeliveryGuaranty deliveryGuaranty = new DeliveryGuaranty(
			DeliveryGuaranty.FAST);

	public NettyUDPMessageSender(SocketAddress remoteAddress,
			DatagramChannel channel)
	{
		this.remoteAddress = remoteAddress;
		this.channel = channel;
	}

	@Override
	public Object sendMessage(Object message)
	{
		return channel.write(message, remoteAddress);
	}

	@Override
	public IDeliveryGuaranty getDeliveryGuaranty()
	{
		return deliveryGuaranty;
	}

	public SocketAddress getRemoteAddress()
	{
		return remoteAddress;
	}

	public void setRemoteAddress(SocketAddress remoteAddress)
	{
		this.remoteAddress = remoteAddress;
	}

	public DatagramChannel getChannel()
	{
		return channel;
	}

	public void setChannel(DatagramChannel channel)
	{
		this.channel = channel;
	}

	@Override
	public String toString()
	{
		String channelId = "UDP Channel with id: ";
		if (null != channel)
		{
			channelId += channel.getId();
		}
		else
		{
			channelId += "0";
		}
		String sender = "Netty " + channelId + " RemoteAddress: "
				+ remoteAddress;
		return sender;
	}
}
