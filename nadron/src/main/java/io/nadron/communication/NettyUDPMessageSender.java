package io.nadron.communication;

import io.nadron.app.Session;
import io.nadron.communication.DeliveryGuaranty.DeliveryGuarantyOptions;
import io.nadron.communication.MessageSender.Fast;
import io.nadron.event.Event;
import io.nadron.event.EventContext;
import io.nadron.event.Events;
import io.nadron.event.impl.DefaultNetworkEvent;
import io.nadron.handlers.netty.UDPUpstreamHandler;
import io.nadron.service.SessionRegistryService;
import io.netty.channel.socket.DatagramChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to send messages to a remote UDP client or server. An
 * instance of this class will be created by the {@link UDPUpstreamHandler} when
 * a {@link Events#CONNECT} event is received from client. The created instance
 * of this class is then sent as payload of a {@link DefaultNetworkEvent} to the
 * {@link Session}.
 * 
 * 
 * @author Abraham Menacherry
 * 
 */
public class NettyUDPMessageSender implements Fast
{
	private static final Logger LOG = LoggerFactory
			.getLogger(NettyUDPMessageSender.class);
	private final SocketAddress remoteAddress;
	private final DatagramChannel channel;
	private final SessionRegistryService<SocketAddress> sessionRegistryService;
	private final EventContext eventContext;
	private static final DeliveryGuaranty DELIVERY_GUARANTY = DeliveryGuarantyOptions.FAST;

	public NettyUDPMessageSender(SocketAddress remoteAddress,
			DatagramChannel channel,
			SessionRegistryService<SocketAddress> sessionRegistryService)
	{
		this.remoteAddress = remoteAddress;
		this.channel = channel;
		this.sessionRegistryService = sessionRegistryService;
		this.eventContext = new EventContextImpl((InetSocketAddress)remoteAddress);
	}

	@Override
	public Object sendMessage(Object message)
	{
		// TODO this might overwrite valid context, check for better design
		if(message instanceof Event){
			((Event)message).setEventContext(eventContext);
		}
		return channel.writeAndFlush(message);
	}

	@Override
	public DeliveryGuaranty getDeliveryGuaranty()
	{
		return DELIVERY_GUARANTY;
	}

	@Override
	public void close()
	{
		Session session = sessionRegistryService.getSession(remoteAddress);
		if (sessionRegistryService.removeSession(remoteAddress))
		{
			LOG.debug("Successfully removed session: {}", session);
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

	protected SessionRegistryService<SocketAddress> getSessionRegistryService()
	{
		return sessionRegistryService;
	}
	
	protected static class EventContextImpl implements EventContext
	{
		final InetSocketAddress clientAddress;
		public EventContextImpl(InetSocketAddress clientAddress){
			this.clientAddress = clientAddress;
		}
		@Override
		public Session getSession() {
			return null;
		}

		@Override
		public void setSession(Session session) {
		}

		@Override
		public InetSocketAddress getAttachment() {
			return clientAddress;
		}

		@Override
		public void setAttachment(Object attachement) {
		}
		
	}
}
