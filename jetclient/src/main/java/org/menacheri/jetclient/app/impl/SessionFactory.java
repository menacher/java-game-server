package org.menacheri.jetclient.app.impl;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.menacheri.jetclient.NettyTCPClient;
import org.menacheri.jetclient.NettyUDPClient;
import org.menacheri.jetclient.app.IPlayer;
import org.menacheri.jetclient.app.IPlayerSession;
import org.menacheri.jetclient.app.ISession;
import org.menacheri.jetclient.app.impl.Session.SessionBuilder;
import org.menacheri.jetclient.communication.IMessageBuffer;
import org.menacheri.jetclient.communication.IMessageSender;
import org.menacheri.jetclient.communication.NettyTCPMessageSender;
import org.menacheri.jetclient.communication.NettyUDPMessageSender;
import org.menacheri.jetclient.event.Events;
import org.menacheri.jetclient.event.IEvent;
import org.menacheri.jetclient.event.IEventHandler;
import org.menacheri.jetclient.handlers.netty.TCPPipelineFactory;
import org.menacheri.jetclient.handlers.netty.UDPPipelineFactory;
import org.menacheri.jetclient.util.LoginHelper;

/**
 * Class used to create a session in jetclient. SessionFactory will also create
 * the actual connection to the jetserver by initializing {@link NettyTCPClient}
 * and {@link NettyUDPClient} and using their connect methods.
 * 
 * @author Abraham Menacherry
 * 
 */
public class SessionFactory
{

	/**
	 * This class holds a number of variables like username, password etc which
	 * are necessary for creating connections to remote jetserver.
	 */
	private final LoginHelper loginHelper;
	private final NettyTCPClient tcpClient;
	private final NettyUDPClient udpClient;
	private static final AtomicInteger sessionId = new AtomicInteger(0);

	/**
	 * This constructor will take a {@link LoginHelper} and initialize the
	 * {@link NettyTCPClient} and {@link NettyUDPClient}s using the connection
	 * parameters provided in this login helper class.
	 * 
	 * @param theLoginHelper
	 * @throws UnknownHostException
	 * @throws Exception
	 */
	public SessionFactory(final LoginHelper theLoginHelper)
			throws UnknownHostException, Exception
	{
		this.loginHelper = theLoginHelper;
		InetSocketAddress tcpAddress = loginHelper.getTcpServerAddress();
		this.tcpClient = new NettyTCPClient(tcpAddress);
		InetSocketAddress udpAddress = loginHelper.getUdpServerAddress();
		if (null == udpAddress)
		{
			udpClient = null;
		}
		else
		{
			udpClient = new NettyUDPClient(udpAddress,
					UDPPipelineFactory.getInstance());
		}
	}

	/**
	 * Creates a {@link ISession} and connects it to the remote jetserver.
	 * 
	 * @return The session instance created.
	 * @throws InterruptedException
	 * @throws Exception
	 */
	public ISession createAndConnectSession() throws InterruptedException,
			Exception
	{
		ISession session = createSession();
		connectSession(session);
		return session;
	}

	/**
	 * @return Returns the session instance created using a
	 *         {@link SessionBuilder}.
	 */
	public ISession createSession()
	{
		SessionBuilder sessionBuilder = new SessionBuilder().id(sessionId
				.incrementAndGet());
		return sessionBuilder.build();
	}

	/**
	 * Connects the session to remote jetserver. Depending on the connection
	 * parameters provided to LoginHelper, it can connect both TCP and UDP
	 * transports.
	 * 
	 * @param session
	 *            The session to be connected to remote jetserver.
	 * @throws InterruptedException
	 * @throws Exception
	 */
	public void connectSession(final ISession session)
			throws InterruptedException, Exception
	{
		InetSocketAddress localAddress = null;
		if (null != udpClient)
		{
			final DatagramChannel datagramChannel = udpClient
					.createDatagramChannel();
			localAddress = udpClient.getLocalAddress(datagramChannel);
			// Add a start event handler to the session which will send the udp
			// connect on server START signal.
			IEventHandler startEventHandler = new IEventHandler()
			{
				@Override
				public void onEvent(IEvent event)
				{
					try
					{
						udpClient.connect(session, datagramChannel);
					}
					catch (UnknownHostException e)
					{
						throw new RuntimeException(e);
					}
					catch (InterruptedException e)
					{
						throw new RuntimeException(e);
					}
				}

				@Override
				public int getEventType()
				{
					return Events.START;
				}
			};
			session.addHandler(startEventHandler);
			IMessageSender udpMessageSender = new NettyUDPMessageSender(
					udpClient.getServerAddress(), datagramChannel);
			session.setUdpMessageSender(udpMessageSender);
		}

		// Connect session using tcp to remote jetserver
		TCPPipelineFactory tcpFactory = new TCPPipelineFactory(session);
		IMessageBuffer<ChannelBuffer> buffer = loginHelper
				.getLoginBuffer(localAddress);
		IEvent loginEvent = Events.event(buffer, Events.LOG_IN);
		// This will in turn invoke the startEventHandler when server sends
		// Events.START event.
		Channel channel = tcpClient.connect(tcpFactory, loginEvent);
		IMessageSender tcpMessageSender = new NettyTCPMessageSender(channel);
		session.setTcpMessageSender(tcpMessageSender);
	}

	public IPlayerSession createPlayerSession(IPlayer player)
	{
		SessionBuilder sessionBuilder = new SessionBuilder();
		PlayerSession playerSession = new PlayerSession(sessionBuilder, player);
		return playerSession;
	}

	public LoginHelper getLoginHelper()
	{
		return loginHelper;
	}

	public NettyTCPClient getTcpClient()
	{
		return tcpClient;
	}

	public NettyUDPClient getUdpClient()
	{
		return udpClient;
	}

}
