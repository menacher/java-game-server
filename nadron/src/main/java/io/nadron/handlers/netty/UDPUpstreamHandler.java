package io.nadron.handlers.netty;

import io.nadron.app.Session;
import io.nadron.communication.MessageSender.Fast;
import io.nadron.communication.NettyUDPMessageSender;
import io.nadron.event.Event;
import io.nadron.event.Events;
import io.nadron.service.SessionRegistryService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;

import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UDPUpstreamHandler extends SimpleChannelInboundHandler<DatagramPacket>
{
	private static final Logger LOG = LoggerFactory.getLogger(UDPUpstreamHandler.class);
	private static final String UDP_CONNECTING = "UDP_CONNECTING";
	private SessionRegistryService<SocketAddress> udpSessionRegistry;
	private MessageBufferEventDecoder messageBufferEventDecoder;
	
	public UDPUpstreamHandler()
	{
		super();
	}
	
	@Override
	public void channelRead0(ChannelHandlerContext ctx,
			DatagramPacket packet) throws Exception
	{
		// Get the session using the remoteAddress.
		SocketAddress remoteAddress = packet.sender();
		Session session = udpSessionRegistry.getSession(remoteAddress);
		if (null != session) 
		{
			ByteBuf buffer = packet.content();
			Event event = (Event) messageBufferEventDecoder
					.decode(null, buffer);

			// If the session's UDP has not been connected yet then send a
			// CONNECT event.
			if (!session.isUDPEnabled()) 
			{
				if (null == session.getAttribute(UDP_CONNECTING)
						|| (!(Boolean) session.getAttribute(UDP_CONNECTING))) 
				{
					session.setAttribute(UDP_CONNECTING, true);
					event = getUDPConnectEvent(event, remoteAddress,
							(DatagramChannel) ctx.channel());
					// Pass the connect event on to the session
					session.onEvent(event);
				}
				else
				{
					LOG.info("Going to discard UDP Message Event with type {} "
							+ "the UDP MessageSender is not initialized fully",
							event.getType());
				}
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
			LOG.trace(
					"Packet received from unknown source address: {}, going to discard",
					remoteAddress);
		}
	}

	public Event getUDPConnectEvent(Event event, SocketAddress remoteAddress,
			DatagramChannel udpChannel)
	{
		LOG.debug("Incoming udp connection remote address : {}",
				remoteAddress);
		
		if (event.getType() != Events.CONNECT)
		{
			LOG.info("UDP Event with type {} will get converted to a CONNECT "
					+ "event since the UDP MessageSender is not initialized till now",
					event.getType());
		}
		Fast messageSender = new NettyUDPMessageSender(remoteAddress, udpChannel, udpSessionRegistry);
		Event connectEvent = Events.connectEvent(messageSender);
		
		return connectEvent;
	}

	public SessionRegistryService<SocketAddress> getUdpSessionRegistry()
	{
		return udpSessionRegistry;
	}

	public void setUdpSessionRegistry(
			SessionRegistryService<SocketAddress> udpSessionRegistry)
	{
		this.udpSessionRegistry = udpSessionRegistry;
	}
	
	public MessageBufferEventDecoder getMessageBufferEventDecoder() 
	{
		return messageBufferEventDecoder;
	}

	public void setMessageBufferEventDecoder(
			MessageBufferEventDecoder messageBufferEventDecoder) 
	{
		this.messageBufferEventDecoder = messageBufferEventDecoder;
	}

	
}
