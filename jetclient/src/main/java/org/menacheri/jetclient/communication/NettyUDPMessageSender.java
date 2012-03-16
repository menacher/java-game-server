package org.menacheri.jetclient.communication;

import java.net.SocketAddress;

import org.jboss.netty.channel.socket.DatagramChannel;
import org.menacheri.jetclient.NettyUDPClient;
import org.menacheri.jetclient.app.ISession;
import org.menacheri.jetclient.communication.IMessageSender.IFast;
import org.menacheri.jetclient.event.Events;

/**
 * This class is used to send messages to a remote UDP client or server. An
 * instance of this class will be created when the {@link Events#CONNECT_UDP}
 * event is sent to a {@link ISession}
 * 
 * @author Abraham Menacherry
 * 
 */
public class NettyUDPMessageSender implements IFast
{
	private final SocketAddress remoteAddress;
	private final DatagramChannel channel;
	private static final IDeliveryGuaranty DELIVERY_GUARANTY = IDeliveryGuaranty.DeliveryGuaranty.FAST;

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
		return DELIVERY_GUARANTY;
	}

	@Override
	public void close()
	{
		ISession session = NettyUDPClient.CLIENTS.remove(channel
				.getLocalAddress());
		if (null == session)
		{
			System.err.println("Possible memory leak occurred. "
					+ "The session associated with udp localaddress: "
					+ channel.getLocalAddress()
					+ " could not be removed from NettyUDPClient.CLIENTS map");
		}
		channel.close();
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
