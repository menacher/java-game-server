package org.menacheri.jetclient;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.socket.DatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.menacheri.jetclient.app.ISession;
import org.menacheri.jetclient.event.Events;
import org.menacheri.jetclient.event.IEvent;
import org.menacheri.jetclient.handlers.netty.UDPUpstreamHandler;

/**
 * This client class is used for UDP communication with a remote jetserver. Same
 * client instance can be used to create multiple UDP "connections" to same
 * jetserver. For connecting with multiple jetserver's use multiple instances of
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
	 * The worker executor which will provide threads to Netty
	 * {@link ChannelFactory} for decoding encoding done on the
	 * {@link ChannelPipeline}.
	 */
	private final ExecutorService worker;
	private final ConnectionlessBootstrap udpBootstrap;
	/**
	 * The instance of {@link NioDatagramChannelFactory} created by constructor,
	 * or the one passed in to constructor.
	 */
	private final DatagramChannelFactory channelFactory;
	/**
	 * For UDP there can only be one pipelineFactory per
	 * {@link ConnectionlessBootstrap}. This factory is hence part of the client
	 * class.
	 */
	private final ChannelPipelineFactory pipelineFactory;
	/**
	 * This map is used to store the local address to which a session has bound
	 * itself using the {@link DatagramChannel#bind(java.net.SocketAddress)}
	 * method. When an incoming UDP packet is recieved the
	 * {@link UDPUpstreamHandler} will resolve which session to pass the event,
	 * using this map.
	 */
	public static final Map<InetSocketAddress, ISession> CLIENTS = new HashMap<InetSocketAddress, ISession>();

	/**
	 * Creates an instance of a Netty UDP client which can then be used to
	 * connect to a remote jet-server. This constructor delegates to
	 * {@link #NettyUDPClient(InetSocketAddress, ChannelPipelineFactory)}
	 * constructor after creating a {@link InetSocketAddress} instance based on
	 * the host and port number passed in.
	 * 
	 * @param jetserverHost
	 *            The host name of the remote server on which jetserver is
	 *            running.
	 * @param port
	 *            The port to connect to, on the remote server.
	 * @param pipelineFactory
	 *            The pipeline factory to be used while creating a Netty
	 *            {@link Channel}
	 * @throws UnknownHostException
	 * @throws Exception
	 */
	public NettyUDPClient(String jetserverHost, int port,
			final ChannelPipelineFactory pipelineFactory)
			throws UnknownHostException, Exception
	{
		this(new InetSocketAddress(jetserverHost, port), pipelineFactory);
	}

	public NettyUDPClient(final InetSocketAddress serverAddress,
			final ChannelPipelineFactory pipelineFactory)
			throws UnknownHostException, Exception
	{
		this(serverAddress, pipelineFactory, null, Executors
				.newCachedThreadPool());
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
	 * @param channelFactory
	 *            <b>Can be provided as null</b>. If so, it will by default use
	 *            {@link NioDatagramChannelFactory}. If not null, then the
	 *            provided factory is set.
	 * @param worker
	 *            The executor used for creating worker threads. Can be null if
	 *            channelFactory parameter is <b>Not</b> null.
	 * @throws UnknownHostException
	 */
	public NettyUDPClient(final InetSocketAddress serverAddress,
			final ChannelPipelineFactory pipelineFactory,
			final DatagramChannelFactory channelFactory,
			final ExecutorService worker) throws UnknownHostException,
			Exception
	{
		this.worker = worker;
		this.serverAddress = serverAddress;
		if (channelFactory == null)
		{
			this.channelFactory = new NioDatagramChannelFactory(worker);
		}
		else
		{
			this.channelFactory = channelFactory;
		}
		this.udpBootstrap = new ConnectionlessBootstrap(this.channelFactory);
		udpBootstrap.setOption("broadcast", "true");
		this.pipelineFactory = pipelineFactory;
		// The pipeline factory should not be set on the udpBootstrap since it
		// invalidates the getPipeline.
		udpBootstrap.setPipeline(pipelineFactory.getPipeline());
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				udpBootstrap.releaseExternalResources();
			}
		});
	}

	/**
	 * Creates a new datagram channel instance using the {@link #udpBootstrap}
	 * by binding to local host. This method delegates to
	 * {@link #createDatagramChannel(String)} internally, by passing the
	 * localhost's host name to it.
	 * 
	 * @return The newly created instance of the datagram channel.
	 * @throws UnknownHostException
	 */
	public DatagramChannel createDatagramChannel() throws UnknownHostException
	{
		return createDatagramChannel(InetAddress.getLocalHost()
				.getHostAddress());
	}

	/**
	 * Creates a new datagram channel instance using the {@link #udpBootstrap}
	 * by binding to local host.
	 * 
	 * @param localhostName
	 *            The host machine (for e.g. 'localhost') to which it needs to
	 *            bind to. This is <b>Not</b> the remote jet-server hostname.
	 * @return The newly created instance of the datagram channel.
	 * @throws UnknownHostException
	 */
	public DatagramChannel createDatagramChannel(String localhostName)
			throws UnknownHostException
	{
		DatagramChannel datagramChannel = (DatagramChannel) udpBootstrap
				.bind(new InetSocketAddress(localhostName, 0));
		return datagramChannel;
	}

	/**
	 * This method will connect the datagram channel with the server and send
	 * the {@link Events#CONNECT_UDP} message to server. This method will use
	 * {@link #serverAddress} by default when sending the
	 * {@link Events#CONNECT_UDP} message. <b>Note</b> Even if this connect
	 * message does not reach server, the first UDP message that the server
	 * receives from this particular DatagramChannels local address will be
	 * converted by server and used as {@link Events#CONNECT_UDP}.
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
	public ChannelFuture connect(ISession session,
			DatagramChannel datagramChannel) throws UnknownHostException,
			InterruptedException
	{
		return connect(session, datagramChannel, this.serverAddress, 5,
				TimeUnit.SECONDS);
	}

	/**
	 * This method will connect the datagram channel with the server and send
	 * the {@link Events#CONNECT_UDP} message to server.
	 * 
	 * @param session
	 *            The session for which the datagram channel is being created.
	 * @param datagramChannel
	 *            The channel on which the message is to be sent to remote
	 *            server.
	 * @param serverAddress
	 *            The remote address of the server to which to send this
	 *            message.
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
	public ChannelFuture connect(ISession session,
			DatagramChannel datagramChannel, InetSocketAddress serverAddress,
			int timeout, TimeUnit unit) throws UnknownHostException,
			InterruptedException
	{
		if (null == datagramChannel)
		{
			throw new NullPointerException(
					"DatagramChannel passed to connect method cannot be null");
		}
		if (!datagramChannel.isBound())
		{
			throw new IllegalStateException("DatagramChannel: "
					+ datagramChannel
					+ " Passed to connect method is not bound");
		}

		IEvent event = Events.event(null, Events.CONNECT_UDP);
		ChannelFuture future = datagramChannel.write(event, serverAddress);
		future.addListener(new ChannelFutureListener()
		{
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception
			{
				if (!future.isSuccess())
				{
					throw new RuntimeException(future.getCause());
				}
			}
		});
		CLIENTS.put(datagramChannel.getLocalAddress(), session);
		return future;
	}

	/**
	 * Utility method used to send a message to the server. Users can also use
	 * datagramChannel.write(message, serverAddress) directly. This method
	 * delegates to {@link #write(DatagramChannel, Object, InetSocketAddress)}
	 * by passing in the InetSocketAddress stored in the class variable
	 * {@link #serverAddress}
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
	public ChannelFuture write(DatagramChannel datagramChannel, Object message)
	{
		return write(datagramChannel, message, serverAddress);
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
	public static ChannelFuture write(DatagramChannel datagramChannel, Object message,
			InetSocketAddress serverAddress)
	{
		return datagramChannel.write(message, serverAddress);
	}

	public InetSocketAddress getLocalAddress(DatagramChannel c)
	{
		InetSocketAddress add = (InetSocketAddress) c.getLocalAddress();
		return add;
	}

	public InetSocketAddress getServerAddress()
	{
		return serverAddress;
	}

	public ExecutorService getWorker()
	{
		return worker;
	}

	public ConnectionlessBootstrap getUdpBootstrap()
	{
		return udpBootstrap;
	}

	public DatagramChannelFactory getChannelFactory()
	{
		return channelFactory;
	}

	public ChannelPipelineFactory getPipelineFactory()
	{
		return pipelineFactory;
	}

}
