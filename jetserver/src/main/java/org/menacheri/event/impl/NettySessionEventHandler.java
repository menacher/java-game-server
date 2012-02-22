package org.menacheri.event.impl;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.menacheri.communication.IMessageBuffer;
import org.menacheri.communication.IMessageSender;
import org.menacheri.communication.NettyMessageBuffer;
import org.menacheri.communication.NettyTCPMessageSender;
import org.menacheri.communication.NettyUDPMessage;
import org.menacheri.communication.NettyUDPMessageSender;
import org.menacheri.context.AppContext;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.service.ISessionRegistryService;
import org.menacheri.util.NettyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NettySessionEventHandler extends AbstractSessionEventHandler
{
	private static final Logger LOG = LoggerFactory
			.getLogger(NettySessionEventHandler.class);

	@Override
	public void onLoginUdp(IEvent event)
	{
		// TODO can this be injected?
		ISessionRegistryService udpSessions = (ISessionRegistryService) AppContext
				.getBean(AppContext.SESSION_REGISTRY_SERVICE);

		// TODO Below line will break if we switch protocols.
		@SuppressWarnings({ "unchecked", "rawtypes" })
		IMessageBuffer<ChannelBuffer> buffer = (IMessageBuffer) event.getSource();
		if (null != udpSessions)
		{
			InetSocketAddress remoteAddress = NettyUtils
					.readSocketAddress(buffer.getNativeBuffer());
			if (udpSessions.putSession(remoteAddress, getSession()))
			{
				NettyMessageBuffer messageBuffer = new NettyMessageBuffer();
				messageBuffer.writeString(remoteAddress.getHostName());
				messageBuffer.writeInt(remoteAddress.getPort());
				IEvent loginSuccess = Events.event(messageBuffer,
						Events.LOG_IN_SUCCESS);
				getSession().onEvent(loginSuccess);
			}
			else
			{
				IEvent loginFailure = Events.event(null, Events.LOG_IN_FAILURE);
				getSession().onEvent(loginFailure);
			}
		}
	}

	@Override
	public void onTcpConnect(IEvent event)
	{
		super.onTcpConnect(event);

		// Create and send a start event to client.
		// TODO this code is NOT going to work if there are some other encoders
		// defined in the pipeline.
		NettyMessageBuffer messageBuffer = new NettyMessageBuffer();
		messageBuffer.writeByte(Events.START);
		IEvent startEvent = Events.event(messageBuffer, Events.START);
		onStart(startEvent);
	}

	@Override
	public synchronized IMessageSender createMessageSender(
			Object nativeConnection)
	{
		IMessageSender messageSender = null;
		if (nativeConnection instanceof Channel)
		{
			Channel channel = (Channel) nativeConnection;
			// Create the message sender implementation, default TCP.
			messageSender = createTCPMessageSender(channel);
		}
		else if (nativeConnection instanceof NettyUDPMessage)
		{
			NettyUDPMessage udpConnection = (NettyUDPMessage) nativeConnection;
			messageSender = createUDPMessageSender(udpConnection);
		}
		LOG.trace("Created message sender: {}", messageSender);
		return messageSender;
	}

	public IMessageSender createTCPMessageSender(Channel channel)
	{
		LOG.trace("Creating TCP Message sender");
		return new NettyTCPMessageSender(channel);
	}

	/**
	 * Multiple invocations of this should not cause much of an issue. It would
	 * just result in an unnecessary object creation without other race issues.
	 * 
	 * @param udpConnection
	 * @return Returns a new instance of {@link IMessageSender} which can be
	 *         used for UDP transmission.
	 */
	public IMessageSender createUDPMessageSender(NettyUDPMessage udpConnection)
	{
		LOG.trace("Creating UDP Message sender");
		DatagramChannel udpChannel = udpConnection.getChannel();
		SocketAddress remoteAddress = udpConnection.getSocketAddress();
		return new NettyUDPMessageSender(remoteAddress, udpChannel);
	}

	@Override
	public synchronized void onClose(IEvent event)
	{
		super.onClose(event);
		
		LOG.trace("In onClose method of class: {}", this.getClass().getName());

		// Close the tcpSender.
		if (null != tcpSender)
		{
			LOG.info("Going to close tcp connection in class: {}", this
					.getClass().getName());
			NettyTCPMessageSender nettyTcpSender = (NettyTCPMessageSender) tcpSender;
			// Close the channel after flushing the pending writes.
			nettyTcpSender.close(Events.event(null, Events.DISCONNECT));
		}
		else
		{
			LOG.warn("TCPSender is null in class {}, no need to close", this
					.getClass().getName());
		}

		if (null != udpSender)
		{
			// Close the udpSender
			LOG.info("Going to remove session from sessions map in class: {}",
					this.getClass().getName());
			
			NettyUDPMessageSender nettyUdpSender = (NettyUDPMessageSender) udpSender;
			SocketAddress remoteAddress = nettyUdpSender.getRemoteAddress();
			ISessionRegistryService udpSessions = (ISessionRegistryService) AppContext
					.getBean(AppContext.SESSION_REGISTRY_SERVICE);
			if (udpSessions.removeSession(remoteAddress))
			{
				LOG.info("Successfully removed session: {}", getSession());
			}
			else
			{
				LOG.warn("No session found for address: {} in class: {}",
						remoteAddress, this.getClass().getName());
			}
		}
		else
		{
			LOG.warn("UDPSender is null in class {}, no need to close", this
					.getClass().getName());
		}
	}

}
