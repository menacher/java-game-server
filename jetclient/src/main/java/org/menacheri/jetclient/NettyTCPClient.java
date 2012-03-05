package org.menacheri.jetclient;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.menacheri.jetclient.event.IEvent;

/**
 * Java client class that provides a TCP transport network connection to remote
 * jetserver. Using one instance of this class, multiple tcp connections can be
 * made to a remote jetserver. For connection to multiple jetserver's use
 * multiple instances of this class.
 * 
 * @author Abraham Menacherry.
 * 
 */
public class NettyTCPClient
{
	/**
	 * The remote server address to which this client should connect.
	 */
	private final InetSocketAddress serverAddress;
	/**
	 * The instance of {@link NioClientSocketChannelFactory} created by
	 * constructor, or the one passed in to constructor.
	 */
	private final ChannelFactory channelFactory;
	/**
	 * The boss executor which will provide threads to Netty
	 * {@link ChannelFactory} for reading from the NIO selectors.
	 */
	private final ExecutorService boss;
	/**
	 * The worker executor which will provide threads to Netty
	 * {@link ChannelFactory} for decoding encoding done on the
	 * {@link ChannelPipeline}.
	 */
	private final ExecutorService worker;
	private final ClientBootstrap bootstrap;
	/**
	 * The amount of time in seconds to wait for this client to close all
	 * {@link Channel}s and shutdown gracefully.
	 */
	private final int maxShutdownWaitTime;
	/**
	 * Any successful TCP connection opened by the client to server is also
	 * added to this {@link ChannelGroup}. This will be used for shutting down
	 * the client gracefully.
	 */
	public static final ChannelGroup ALL_CHANNELS = new DefaultChannelGroup(
			"JET-CLIENT-CONNECTIONS");

	/**
	 * Creates an instance of a Netty TCP client which can then be used to
	 * connect to a remote jet-server. This constructor delegates to
	 * {@link #NettyTCPClient(InetSocketAddress)} constructor after creating a
	 * {@link InetSocketAddress} instance based on the host and port number
	 * passed in.
	 * 
	 * @param jetserverHost
	 *            The host name of the remote server on which jetserver is
	 *            running.
	 * @param port
	 *            The port to connect to, on the remote server.
	 */
	public NettyTCPClient(String jetserverHost, int port)
	{
		this(new InetSocketAddress(jetserverHost, port));
	}

	public NettyTCPClient(final InetSocketAddress serverAddress)
	{
		this(serverAddress, Executors.newCachedThreadPool(), Executors
				.newCachedThreadPool(), null, 5000);
	}

	/**
	 * Creates a new instance of the {@link NettyTCPClient}. This constructor
	 * also registers a shutdown hook which will call close on
	 * {@link #ALL_CHANNELS} and call bootstrap.releaseExternalResources() to
	 * enable a graceful shutdown.
	 * 
	 * @param serverAddress
	 *            The remote servers address. This address will be used when any
	 *            of the default write/connect methods are used.
	 * @param boss
	 *            {@link Executor} used for creating the {@link #channelFactory}
	 *            instance. Can be <b>null</b> if {@link #channelFactory} is not
	 *            null.
	 * @param worker
	 *            {@link Executor} used for creating the {@link #channelFactory}
	 *            instance. Can be <b>null</b> if {@link #channelFactory} is not
	 *            null.
	 * @param channelFactory
	 *            <b>Can be provided as null</b>. If so, it will by default use
	 *            {@link NioClientSocketChannelFactory}. If not null, then the
	 *            provided factory is set.
	 * @param maxShutdownWaitTime
	 *            The amount of time in seconds to wait for this client to close
	 *            all {@link Channel}s and shutdown gracefully.
	 */
	public NettyTCPClient(final InetSocketAddress serverAddress,
			final ExecutorService boss, final ExecutorService worker,
			final ChannelFactory channelFactory, final int maxShutdownWaitTime)
	{
		this.serverAddress = serverAddress;
		this.boss = boss;
		this.worker = worker;
		if (null != channelFactory)
		{
			this.channelFactory = channelFactory;
		}
		else
		{
			this.channelFactory = new NioClientSocketChannelFactory(boss,
					worker);
		}
		this.bootstrap = new ClientBootstrap(this.channelFactory);
		// At client side option is tcpNoDelay and at server child.tcpNoDelay
		this.bootstrap.setOption("tcpNoDelay", true);
		this.bootstrap.setOption("keepAlive", true);
		this.maxShutdownWaitTime = maxShutdownWaitTime;
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				ChannelGroupFuture future = ALL_CHANNELS.close();
				try
				{
					future.await(NettyTCPClient.this.maxShutdownWaitTime);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				bootstrap.releaseExternalResources();
			}
		});
	}

	/**
	 * This method delegates to the
	 * {@link #connect(ChannelPipelineFactory, IEvent, int, TimeUnit)}
	 * method internally. It will pass in a default of 5 seconds wait time to
	 * the delegated method.
	 * 
	 * @param pipelineFactory
	 *            The factory used to create a pipeline of decoders and encoders
	 *            for each {@link Channel} that it creates on connection.
	 * @param loginEvent
	 *            The event contains the {@link ChannelBuffer} to be transmitted
	 *            to jetserver for logging in. Values inside this buffer include
	 *            username, password, connection key, <b>optional</b> local
	 *            address of the UDP channel used by this session.
	 * @return Returns the Netty {@link Channel} which is the connection to the
	 *         remote jetserver.
	 * @throws InterruptedException
	 */
	public Channel connect(final ChannelPipelineFactory pipelineFactory,
			final IEvent loginEvent)
			throws InterruptedException
	{
		return connect(pipelineFactory, loginEvent, 5, TimeUnit.SECONDS);
	}

	/**
	 * Method that is used to create the connection or {@link Channel} to
	 * communicated with the remote jetserver.
	 * 
	 * @param pipelineFactory
	 *            The factory used to create a pipeline of decoders and encoders
	 *            for each {@link Channel} that it creates on connection.
	 * @param loginEvent
	 *            The event contains the {@link ChannelBuffer} to be transmitted
	 *            to jetserver for logging in. Values inside this buffer include
	 *            username, password, connection key, <b>optional</b> local
	 *            address of the UDP channel used by this session.
	 * @param timeout
	 *            The amount of time to wait for this connection be created
	 *            successfully.
	 * @param unit
	 *            The unit of timeout SECONDS, MILLISECONDS etc. Default is 5
	 *            seconds.
	 * @return Returns the Netty {@link Channel} which is the connection to the
	 *         remote jetserver.
	 * @throws InterruptedException
	 */
	public Channel connect(final ChannelPipelineFactory pipelineFactory,
			final IEvent loginEvent, int timeout, TimeUnit unit)
			throws InterruptedException
	{
		ChannelFuture future;
		synchronized (bootstrap)
		{
			bootstrap.setPipelineFactory(pipelineFactory);
			future = bootstrap.connect(serverAddress);
			future.addListener(new ChannelFutureListener()
			{
				@Override
				public void operationComplete(ChannelFuture future)
						throws Exception
				{
					if (future.isSuccess())
					{
						future.getChannel().write(loginEvent);
					}
					else
					{
						throw new RuntimeException(future.getCause()
								.getMessage());
					}
				}
			});
		}
		return future.getChannel();
	}

	public InetSocketAddress getServerAddress()
	{
		return serverAddress;
	}

	public ChannelFactory getChannelFactory()
	{
		return channelFactory;
	}

	public ExecutorService getBoss()
	{
		return boss;
	}

	public ExecutorService getWorker()
	{
		return worker;
	}

	public ClientBootstrap getBootstrap()
	{
		return bootstrap;
	}

	public int getMaxShutdownWaitTime()
	{
		return maxShutdownWaitTime;
	}

}
