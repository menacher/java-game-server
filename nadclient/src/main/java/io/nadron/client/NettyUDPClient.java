package io.nadron.client;

import io.nadron.client.app.Session;
import io.nadron.client.event.Event;
import io.nadron.client.event.Events;
import io.nadron.client.handlers.netty.UDPUpstreamHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * This client class is used for UDP communication with a remote nadron server. Same
 * client instance can be used to create multiple UDP "connections" to same
 * nadron server. For connecting with multiple nadron server's use multiple instances of
 * this class.
 * 
 * @author Abraham Menacherry
 * 
 */
public class NettyUDPClient
{
	/**
	 * The remote server address to which this client should connect.
	 */
	private final InetSocketAddress serverAddress;
	/**
	 * The boss executor which will provide threads to Netty
	 * {@link ChannelFactory} for reading from the NIO selectors.
	 */
	private final EventLoopGroup boss;
	/**
	 * For UDP there can only be one pipelineFactory per
	 * {@link Bootstrap}. This factory is hence part of the client
	 * class.
	 */
	private final ChannelInitializer<DatagramChannel> pipelineFactory;
	
	/**
	 * This map is used to store the local address to which a session has bound
	 * itself using the {@link DatagramChannel#bind(java.net.SocketAddress)}
	 * method. When an incoming UDP packet is recieved the
	 * {@link UDPUpstreamHandler} will resolve which session to pass the event,
	 * using this map.
	 */
	public static final Map<InetSocketAddress, Session> CLIENTS = new HashMap<InetSocketAddress, Session>();

	/**
	 * Creates an instance of a Netty UDP client which can then be used to
	 * connect to a remote Nadron server. This constructor delegates to
	 * {@link #NettyUDPClient(InetSocketAddress, ChannelInitializer, String)}
	 * constructor after creating a {@link InetSocketAddress} instance based on
	 * the host and port number passed in.
	 * 
	 * @param nadronHost
	 *            The host name of the remote server on which nadron server is
	 *            running.
	 * @param port
	 *            The port to connect to, on the remote server.
	 * @param pipelineFactory
	 *            The pipeline factory to be used while creating a Netty
	 *            {@link Channel}
	 * @throws UnknownHostException
	 * @throws Exception
	 */
	public NettyUDPClient(String nadronHost, int port,
			final ChannelInitializer<DatagramChannel> pipelineFactory)
			throws UnknownHostException, Exception
	{
		this(new InetSocketAddress(nadronHost, port), pipelineFactory, null);
	}

	/**
	 * Creates a new instance of the {@link NettyUDPClient}. It actually
	 * delegates to
	 * {@link #NettyUDPClient(InetSocketAddress, ChannelInitializer, EventLoopGroup, String)}
	 * . It will internally instantiate the {@link EventLoopGroup}.
	 * 
	 * @param serverAddress
	 *            The remote servers address. This address will be used when any
	 *            of the default write/connect methods are used.
	 * @param pipelineFactory
	 *            The Netty factory used for creating a pipeline. For UDP, this
	 *            pipeline factory should not have any stateful i.e non
	 *            share-able handlers in it. Since Netty only has one channel
	 *            for <b>ALL</b> UPD traffic.
	 * @param localhostName
	 *            Name of the host to which this client is to be bound.
	 *            Generally localhost. If null, then
	 *            <code>InetAddress.getLocalHost().getHostAddress()</code> is
	 *            used internally by default.
	 * @throws UnknownHostException
	 *             , Exception
	 */
	public NettyUDPClient(final InetSocketAddress serverAddress,
			final ChannelInitializer<DatagramChannel> pipelineFactory, String localhostName)
			throws UnknownHostException, Exception
	{
		this(serverAddress, pipelineFactory, new NioEventLoopGroup(), localhostName);
	}

	/**
	 * Creates a new instance of the {@link NettyUDPClient}.
	 * 
	 * @param serverAddress
	 *            The remote servers address. This address will be used when any
	 *            of the default write/connect methods are used.
	 * @param pipelineFactory
	 *            The Netty factory used for creating a pipeline. For UDP, this
	 *            pipeline factory should not have any stateful i.e non
	 *            share-able handlers in it. Since Netty only has one channel
	 *            for <b>ALL</b> UPD traffic.
	 * @param boss
	 *            The {@link EventLoopGroup} used for creating boss threads.
	 * @param localhostName
	 *            Name of the host to which this client is to be bound.
	 *            Generally localhost. If null, then
	 *            <code>InetAddress.getLocalHost().getHostAddress()</code> is
	 *            used internally by default.
	 * @throws UnknownHostException
	 */
	public NettyUDPClient(final InetSocketAddress serverAddress,
			final ChannelInitializer<DatagramChannel> pipelineFactory,
			final EventLoopGroup boss, String localhostName) throws UnknownHostException,
			Exception
	{
		this.boss = boss;
		this.serverAddress = serverAddress;
		this.pipelineFactory = pipelineFactory;
		if (null == localhostName) 
		{
			localhostName = InetAddress.getLocalHost().getHostAddress();
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				boss.shutdownGracefully();
			}
		});
	}

	/**
	 * This method will connect the datagram channel with the server and send
	 * the {@link Events#CONNECT} message to server. This method will use
	 * {@link #serverAddress} by default when sending the
	 * {@link Events#CONNECT} message. <b>Note</b> Even if this connect
	 * message does not reach server, the first UDP message that the server
	 * receives from this particular DatagramChannels local address will be
	 * converted by server and used as {@link Events#CONNECT}.
	 * 
	 * @param session
	 *            The session for which the datagram channel is being created.
	 * @param datagramChannel
	 *            The channel on which the message is to be sent to remote
	 *            server.
	 * @return Returns a ChannelFuture which can be used to check the success of
	 *         this operation. <b>NOTE</b> Success in case of UDP means message
	 *         is sent to server. It does not mean that the server has received
	 *         it.
	 * @throws UnknownHostException
	 */
	public ChannelFuture connect(Session session,
			DatagramChannel datagramChannel) throws UnknownHostException,
			InterruptedException
	{
		return connect(session, datagramChannel, this.serverAddress, 5,
				TimeUnit.SECONDS);
	}

	/**
	 * This method delegates to {@link #createDatagramChannel(String)}
	 * internally, by passing the localhost's host name to it.
	 * 
	 * @return The newly created instance of the datagram channel.
	 * @throws UnknownHostException
	 *             , InterruptedException
	 */
	public DatagramChannel createDatagramChannel() throws UnknownHostException, InterruptedException
	{
		return createDatagramChannel(InetAddress.getLocalHost()
				.getHostAddress());
	}

	/**
	 * Creates a new datagram channel instance using the
	 * {@link NioDatagramChannel} by binding to local host.
	 * 
	 * @param localhostName
	 *            The host machine (for e.g. 'localhost') to which it needs to
	 *            bind to. This is <b>Not</b> the remote Nadron server hostname.
	 * @return The newly created instance of the datagram channel.
	 * @throws UnknownHostException
	 */
	public DatagramChannel createDatagramChannel(String localhostName)
			throws UnknownHostException, InterruptedException {
		Bootstrap udpBootstrap = new Bootstrap();
		udpBootstrap.group(boss).channel(NioDatagramChannel.class)
				.option(ChannelOption.SO_BROADCAST, true)
				.handler(pipelineFactory);
		DatagramChannel datagramChannel = (DatagramChannel) udpBootstrap
				.bind(new InetSocketAddress(localhostName, 0)).sync().channel();
		return datagramChannel;
	}
	
	/**
	 * This method will connect the datagram channel with the server and send
	 * the {@link Events#CONNECT} message to server.
	 * 
	 * @param session
	 *            The session for which the datagram channel is being created.
	 * @param datagramChannel
	 *            The channel on which the message is to be sent to remote
	 *            server.
	 * @param serverAddress
	 *            The remote address of the server to which to connect.
	 * @param timeout
	 *            Amount of time to wait for the connection to happen.
	 *            <b>NOTE</b> Since this is UDP there is actually no "real"
	 *            connection.
	 * @return Returns a ChannelFuture which can be used to check the success of
	 *         this operation. <b>NOTE</b> Success in case of UDP means message
	 *         is sent to server. It does not mean that the server has received
	 *         it.
	 * @throws UnknownHostException
	 */
	public ChannelFuture connect(Session session,
			DatagramChannel datagramChannel, InetSocketAddress serverAddress,
			int timeout, TimeUnit unit) throws UnknownHostException,
			InterruptedException
	{
		if (null == datagramChannel)
		{
			throw new NullPointerException(
					"DatagramChannel passed to connect method cannot be null");
		}
		if (!datagramChannel.isActive())
		{
			throw new IllegalStateException("DatagramChannel: "
					+ datagramChannel
					+ " Passed to connect method is not bound");
		}

		Event event = Events.event(null, Events.CONNECT);
		
		ChannelFuture future = datagramChannel.write(event);
		future.addListener(new ChannelFutureListener()
		{
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception
			{
				if (!future.isSuccess())
				{
					throw new RuntimeException(future.cause());
				}
			}
		});
		CLIENTS.put(datagramChannel.localAddress(), session);
		return future;
	}

	/**
	 * Utility method used to send a message to the server. Users can also use
	 * datagramChannel.write(message, serverAddress) directly.
	 * 
	 * @param datagramChannel
	 *            The channel on which the message is to be sent to remote
	 *            server.
	 * @param message
	 *            The message to be sent. <b>NOTE</b> The message should be a
	 *            valid and encode-able by the encoders in the ChannelPipeline
	 *            of this server.
	 * @return Returns a ChannelFuture which can be used to check the success of
	 *         this operation. <b>NOTE</b> Success in case of UDP means message
	 *         is sent to server. It does not mean that the server has received
	 *         it.
	 */
	public static ChannelFuture write(DatagramChannel datagramChannel, Object message)
	{
		return datagramChannel.write(message);
	}

	public InetSocketAddress getServerAddress()
	{
		return serverAddress;
	}

	public EventLoopGroup getBoss() {
		return boss;
	}

	public ChannelInitializer<DatagramChannel> getPipelineFactory() {
		return pipelineFactory;
	}

}
