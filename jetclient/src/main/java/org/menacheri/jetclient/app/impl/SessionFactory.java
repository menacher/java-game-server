package org.menacheri.jetclient.app.impl;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.menacheri.jetclient.NettyTCPClient;
import org.menacheri.jetclient.NettyUDPClient;
import org.menacheri.jetclient.app.Player;
import org.menacheri.jetclient.app.PlayerSession;
import org.menacheri.jetclient.app.Session;
import org.menacheri.jetclient.app.impl.DefaultSession.SessionBuilder;
import org.menacheri.jetclient.communication.MessageBuffer;
import org.menacheri.jetclient.communication.MessageSender.IFast;
import org.menacheri.jetclient.communication.MessageSender.IReliable;
import org.menacheri.jetclient.communication.NettyTCPMessageSender;
import org.menacheri.jetclient.communication.NettyUDPMessageSender;
import org.menacheri.jetclient.event.Events;
import org.menacheri.jetclient.event.Event;
import org.menacheri.jetclient.event.EventHandler;
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
	 * Creates a {@link Session} and connects it to the remote jetserver.
	 * 
	 * @return The session instance created.
	 * @throws InterruptedException
	 * @throws Exception
	 */
	public Session createAndConnectSession() throws InterruptedException,
			Exception
	{
		Session session = createSession();
		connectSession(session);
		return session;
	}

	/**
	 * @return Returns the session instance created using a
	 *         {@link SessionBuilder}.
	 */
	public Session createSession()
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
	public void connectSession(final Session session)
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
			EventHandler startEventHandler = new EventHandler()
			{
				@Override
				public void onEvent(Event event)
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
			IFast udpMessageSender = new NettyUDPMessageSender(
					udpClient.getServerAddress(), datagramChannel);
			session.setUdpMessageSender(udpMessageSender);
		}

		// Connect session using tcp to remote jetserver
		TCPPipelineFactory tcpFactory = new TCPPipelineFactory(session);
		MessageBuffer<ChannelBuffer> buffer = loginHelper
				.getLoginBuffer(localAddress);
		Event loginEvent = Events.event(buffer, Events.LOG_IN);
		// This will in turn invoke the startEventHandler when server sends
		// Events.START event.
		Channel channel = tcpClient.connect(tcpFactory, loginEvent);
		IReliable tcpMessageSender = new NettyTCPMessageSender(channel);
		session.setTcpMessageSender(tcpMessageSender);
	}

	public PlayerSession createPlayerSession(Player player)
	{
		SessionBuilder sessionBuilder = new SessionBuilder();
		DefaultPlayerSession playerSession = new DefaultPlayerSession(sessionBuilder, player);
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
