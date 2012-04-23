package org.menacheri.jetserver.handlers.netty;

import java.net.SocketAddress;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.menacheri.jetserver.app.Session;
import org.menacheri.jetserver.communication.MessageSender;
import org.menacheri.jetserver.communication.NettyUDPMessageSender;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.service.SessionRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UDPUpstreamHandler extends SimpleChannelUpstreamHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(UDPUpstreamHandler.class);
	private SessionRegistryService sessionRegistryService;
	public UDPUpstreamHandler()
	{
		super();
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception
	{
		// Get the session using the remoteAddress.
		SocketAddress remoteAddress = e.getRemoteAddress();
		Session session = sessionRegistryService.getSession(remoteAddress);
		if(null != session)
		{
			Event event = (Event) e.getMessage();
			// If the session's UDP has not been connected yet then send a
			// CONNECT event.
			if (!session.isUDPEnabled())
			{
				event = getUDPConnectEvent(event, remoteAddress,
						(DatagramChannel) e.getChannel());
				// Pass the connect event on to the session
				session.onEvent(event);
			}
			else if (event.getType() == Events.CONNECT)
			{
				// Duplicate connect just discard.
				LOG.trace("Duplicate CONNECT {} received in UDP channel, "
						+ "for session: {} going to discard", event, session);
			}
			else
			{
				// Pass the original event on to the session
				session.onEvent(event);
			}
		}
		else
		{
			LOG.trace("Packet received from unknown source address: {}, going to discard",remoteAddress);
		}
	}

	public Event getUDPConnectEvent(Event event, SocketAddress remoteAddress,
			DatagramChannel udpChannel)
	{
		LOG.debug("Incoming udp connection remote address : {}",
				remoteAddress);
		
		if (event.getType() != Events.CONNECT)
		{
			LOG.warn("Going to discard UDP Message Event with type {} "
					+ "It will get converted to a CONNECT event since "
					+ "the UDP MessageSender is not initialized till now",
					event.getType());
		}
		MessageSender messageSender = new NettyUDPMessageSender(remoteAddress, udpChannel, sessionRegistryService);
		Event connectEvent = Events.connectEvent(messageSender);
		
		return connectEvent;
	}
	
	public SessionRegistryService getSessionRegistryService()
	{
		return sessionRegistryService;
	}

	public void setSessionRegistryService(
			SessionRegistryService sessionRegistryService)
	{
		this.sessionRegistryService = sessionRegistryService;
	}

		
}
