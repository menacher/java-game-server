package org.menacheri.jetserver.communication;

import java.net.SocketAddress;

import org.jboss.netty.channel.socket.DatagramChannel;
import org.menacheri.jetserver.app.ISession;
import org.menacheri.jetserver.communication.IDeliveryGuaranty.DeliveryGuaranty;
import org.menacheri.jetserver.communication.IMessageSender.IFast;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.impl.NetworkEvent;
import org.menacheri.jetserver.handlers.netty.UDPUpstreamHandler;
import org.menacheri.jetserver.service.ISessionRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to send messages to a remote UDP client or server. An
 * instance of this class will be created by the {@link UDPUpstreamHandler} when
 * a {@link Events#CONNECT} event is received from client. The created instance
 * of this class is then sent as payload of a {@link NetworkEvent} to the
 * {@link ISession}.
 * 
 * 
 * @author Abraham Menacherry
 * 
 */
public class NettyUDPMessageSender implements IFast
{
	private static final Logger LOG = LoggerFactory
			.getLogger(NettyUDPMessageSender.class);
	private final SocketAddress remoteAddress;
	private final DatagramChannel channel;
	private final ISessionRegistryService sessionRegistryService;

	private static final IDeliveryGuaranty DELIVERY_GUARANTY = DeliveryGuaranty.FAST;

	public NettyUDPMessageSender(SocketAddress remoteAddress,
			DatagramChannel channel,
			ISessionRegistryService sessionRegistryService)
	{
		this.remoteAddress = remoteAddress;
		this.channel = channel;
		this.sessionRegistryService = sessionRegistryService;
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
		ISession session = sessionRegistryService.getSession(remoteAddress);
		if (sessionRegistryService.removeSession(remoteAddress))
		{
			LOG.info("Successfully removed session: {}", session);
		}
		else
		{
			LOG.trace("No udp session found for address: {}", remoteAddress);
		}

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

	public ISessionRegistryService getSessionRegistryService()
	{
		return sessionRegistryService;
	}
}
