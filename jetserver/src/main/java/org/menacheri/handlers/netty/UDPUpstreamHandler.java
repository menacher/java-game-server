package org.menacheri.handlers.netty;

import java.net.SocketAddress;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.menacheri.app.ISession;
import org.menacheri.communication.IMessageSender;
import org.menacheri.communication.NettyUDPMessageSender;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.service.ISessionRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UDPUpstreamHandler extends SimpleChannelUpstreamHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(UDPUpstreamHandler.class);
	private ISessionRegistryService sessionRegistryService;
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
		ISession session = sessionRegistryService.getSession(remoteAddress);
		if(null != session)
		{
			IEvent event = (IEvent) e.getMessage();
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

	public IEvent getUDPConnectEvent(IEvent event, SocketAddress remoteAddress,
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
		IMessageSender messageSender = new NettyUDPMessageSender(remoteAddress, udpChannel, sessionRegistryService);
		IEvent connectEvent = Events.connectEvent(messageSender);
		
		return connectEvent;
	}
	
	public ISessionRegistryService getSessionRegistryService()
	{
		return sessionRegistryService;
	}

	public void setSessionRegistryService(
			ISessionRegistryService sessionRegistryService)
	{
		this.sessionRegistryService = sessionRegistryService;
	}

		
}
