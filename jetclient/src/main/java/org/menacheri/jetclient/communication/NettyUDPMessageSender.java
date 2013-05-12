package org.menacheri.jetclient.communication;

import io.netty.channel.socket.DatagramChannel;

import java.net.SocketAddress;

import org.menacheri.jetclient.NettyUDPClient;
import org.menacheri.jetclient.app.Session;
import org.menacheri.jetclient.communication.MessageSender.Fast;
import org.menacheri.jetclient.event.Events;

/**
 * This class is used to send messages to a remote UDP client or server. An
 * instance of this class will be created when the {@link Events#CONNECT} event
 * is sent to a {@link Session}
 * 
 * @author Abraham Menacherry
 * 
 */
public class NettyUDPMessageSender implements Fast
{
	private boolean isClosed = false;
	private final SocketAddress remoteAddress;
	private final DatagramChannel channel;
	private static final DeliveryGuaranty DELIVERY_GUARANTY = DeliveryGuaranty.DeliveryGuarantyOptions.FAST;

	public NettyUDPMessageSender(SocketAddress remoteAddress,
			DatagramChannel channel)
	{
		this.remoteAddress = remoteAddress;
		this.channel = channel;
	}

	@Override
	public Object sendMessage(Object message)
	{
		return channel.write(message);
	}

	@Override
	public DeliveryGuaranty getDeliveryGuaranty()
	{
		return DELIVERY_GUARANTY;
	}

	@Override
	public synchronized void close()
	{
		if (isClosed)
			return;
		Session session = NettyUDPClient.CLIENTS.remove(channel.localAddress());
		if (null == session)
		{
			System.err.println("Possible memory leak occurred. "
					+ "The session associated with udp localaddress: "
					+ channel.localAddress()
					+ " could not be removed from NettyUDPClient.CLIENTS map");
		}
		isClosed = true;
	}

	public SocketAddress getRemoteAddress()
	{
		return remoteAddress;
	}

	public DatagramChannel getChannel()
	{
		return channel;
	}

	@Override
	public String toString()
	{
		String channelId = "UDP Channel with id: ";
		if (null != channel)
		{
			channelId += channel.id();
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
