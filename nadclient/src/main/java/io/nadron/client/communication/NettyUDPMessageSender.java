package io.nadron.client.communication;

import io.nadron.client.NettyUDPClient;
import io.nadron.client.app.Session;
import io.nadron.client.communication.MessageSender.Fast;
import io.nadron.client.event.Events;
import io.netty.channel.socket.DatagramChannel;

import java.net.SocketAddress;


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
		String channelId = "UDP Channel: ";
		if (null != channel)
		{
			channelId += channel.toString();
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
