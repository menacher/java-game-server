package io.nadron.client.app.impl;

import io.nadron.client.NettyTCPClient;
import io.nadron.client.NettyUDPClient;
import io.nadron.client.app.Player;
import io.nadron.client.app.PlayerSession;
import io.nadron.client.app.Session;
import io.nadron.client.app.impl.DefaultSession.SessionBuilder;
import io.nadron.client.communication.MessageBuffer;
import io.nadron.client.communication.NettyTCPMessageSender;
import io.nadron.client.communication.NettyUDPMessageSender;
import io.nadron.client.communication.MessageSender.Fast;
import io.nadron.client.communication.MessageSender.Reliable;
import io.nadron.client.event.Event;
import io.nadron.client.event.EventHandler;
import io.nadron.client.event.Events;
import io.nadron.client.event.SessionEventHandler;
import io.nadron.client.handlers.netty.TCPPipelineFactory;
import io.nadron.client.handlers.netty.UDPPipelineFactory;
import io.nadron.client.util.LoginHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Class used to create a session in Nad client. SessionFactory will also create
 * the actual connection to the nadron server by initializing {@link NettyTCPClient}
 * and {@link NettyUDPClient} and using their connect methods.
 * 
 * @author Abraham Menacherry
 * 
 */
public class SessionFactory
{

	/**
	 * This class holds a number of variables like username, password etc which
	 * are necessary for creating connections to remote nadron server.
	 */
	private LoginHelper loginHelper;
	private final NettyTCPClient tcpClient;
	private final NettyUDPClient udpClient;
	private static final AtomicInteger sessionId = new AtomicInteger(0);
	
	private ChannelInitializer<SocketChannel> channelInitializer;
	
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
					UDPPipelineFactory.getInstance(udpAddress), null);
		}
	}

	/**
	 * Creates a {@link Session} and connects it to the remote nadron server.
	 * 
	 * @return The session instance created and connected to remote nadron server.
	 * @throws InterruptedException
	 * @throws Exception
	 */
	public Session createAndConnectSession() throws InterruptedException,
			Exception
	{
		return createAndConnectSession((EventHandler[]) null);
	}

	/**
	 * Creates a {@link Session}, adds the event handlers to the session and
	 * then connects it to the remote nadron server. This way events will not be
	 * lost on connect.
	 * 
	 * @param eventHandlers
	 *            The handlers to be added to listen to session.
	 * @return The session instance created and connected to remote nadron server.
	 * @throws InterruptedException
	 * @throws Exception
	 */
	public Session createAndConnectSession(EventHandler... eventHandlers)
			throws InterruptedException, Exception
	{
		Session session = createSession();
		connectSession(session, eventHandlers);
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
	 * Connects the session to remote nadron server. Depending on the connection
	 * parameters provided to LoginHelper, it can connect both TCP and UDP
	 * transports.
	 * 
	 * @param session
	 *            The session to be connected to remote nadron server.
	 * @throws InterruptedException
	 * @throws Exception
	 */
	public void connectSession(final Session session)
			throws InterruptedException, Exception
	{
		connectSession(session, (EventHandler[]) null);
	}

	/**
	 * Connects the session to remote nadron server. Depending on the connection
	 * parameters provided to LoginHelper, it can connect both TCP and UDP
	 * transports.
	 * 
	 * @param session
	 *            The session to be connected to remote nadron server.
	 * @param eventHandlers
	 *            The handlers to be added to session.
	 * @throws InterruptedException
	 * @throws Exception
	 */
	public void connectSession(final Session session,
			EventHandler... eventHandlers) throws InterruptedException,
			Exception
	{
		InetSocketAddress udpAddress = null;
		if (null != udpClient)
		{
			udpAddress = doUdpConnection(session);
		}

		if (null != eventHandlers)
		{
			for (EventHandler eventHandler : eventHandlers)
			{
				session.addHandler(eventHandler);
				if (eventHandler instanceof SessionEventHandler)
				{
					((SessionEventHandler) eventHandler).setSession(session);
				}
			}
		}

		MessageBuffer<ByteBuf> buffer = loginHelper
				.getLoginBuffer(udpAddress);
		Event loginEvent = Events.event(buffer, Events.LOG_IN);
		doTcpConnection(session, loginEvent);
	}

	/**
	 * Method used to reconnect existing session which probably got disconnected
	 * due to some exception. It will first close existing tcp and udp
	 * connections and then try re-connecting using the reconnect key from
	 * server.
	 * 
	 * @param session
	 *            The session which needs to be re-connected.
	 * @param reconnectKey
	 *            This is provided by the server on
	 *            {@link Events#GAME_ROOM_JOIN_SUCCESS} event and stored in the
	 *            session.
	 * @throws InterruptedException
	 * @throws Exception
	 */
	public void reconnectSession(final Session session, String reconnectKey)
			throws InterruptedException, Exception
	{
		session.getTcpMessageSender().close();
		if (null != session.getUdpMessageSender())
			session.getUdpMessageSender().close();

		InetSocketAddress udpAddress = null;
		if (null != udpClient)
		{
			udpAddress = doUdpConnection(session);
		}

		Event reconnectEvent = Events.event(
				loginHelper.getReconnectBuffer(reconnectKey, udpAddress),
				Events.RECONNECT);

		doTcpConnection(session, reconnectEvent);
	}

	protected void doTcpConnection(final Session session, Event event)
			throws Exception, InterruptedException
	{
		// This will in turn invoke the startEventHandler when server sends
		// Events.START event.
		Channel channel = tcpClient.connect(getTCPPipelineFactory(session), event);
		if (null != channel)
		{
			Reliable tcpMessageSender = new NettyTCPMessageSender(channel);
			session.setTcpMessageSender(tcpMessageSender);
		}
		else
		{
			throw new Exception("Could not create TCP connection to server");
		}
	}

	/**
	 * Return the pipeline factory or create the default messagebufferprotocol.
	 * 
	 * @param session
	 *            The final handler in the protocol chain needs the session so
	 *            that it can send messages to it.
	 * @return
	 */
	protected synchronized ChannelInitializer<SocketChannel> getTCPPipelineFactory(
			final Session session) {
		if (null == channelInitializer) {
			channelInitializer = new TCPPipelineFactory(session);
		}
		return channelInitializer;
	}
	
	/**
	 * Set the channel initializer. This will be used when connecting the session.
	 * @param channelInitializer
	 */
	protected synchronized void setTCPChannelInitializer(
			ChannelInitializer<SocketChannel> channelInitializer) {
		this.channelInitializer = channelInitializer;
	}
	
	protected InetSocketAddress doUdpConnection(final Session session)
			throws UnknownHostException, InterruptedException
	{
		InetSocketAddress localAddress;
		final DatagramChannel datagramChannel = udpClient
				.createDatagramChannel();
		localAddress = datagramChannel.localAddress();
		// Add a start event handler to the session which will send the udp
		// connect on server START signal.
		final EventHandler startEventHandler = new EventHandler()
		{
			@Override
			public void onEvent(Event event)
			{
				try
				{
					udpClient.connect(session, datagramChannel);
					// remove after use
					session.removeHandler(this);
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
		Fast udpMessageSender = new NettyUDPMessageSender(
				udpClient.getServerAddress(), datagramChannel);
		session.setUdpMessageSender(udpMessageSender);
		return localAddress;
	}

	public PlayerSession createPlayerSession(Player player)
	{
		SessionBuilder sessionBuilder = new SessionBuilder();
		DefaultPlayerSession playerSession = new DefaultPlayerSession(
				sessionBuilder, player);
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

	public void setLoginHelper(LoginHelper loginHelper) {
		this.loginHelper = loginHelper;
	}

}
