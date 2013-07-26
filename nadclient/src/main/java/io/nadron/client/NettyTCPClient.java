package io.nadron.client;

import io.nadron.client.event.Event;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ChannelFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


/**
 * Java client class that provides a TCP transport network connection to remote
 * nadron server. Using one instance of this class, multiple tcp connections can be
 * made to a remote nadron server. For connection to multiple nadron server's use
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
	 * The boss executor which will provide threads to Netty
	 * {@link ChannelFactory} for reading from the NIO selectors.
	 */
	private final EventLoopGroup boss;
	private final Bootstrap bootstrap;
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
			"NAD-CLIENT-CONNECTIONS", GlobalEventExecutor.INSTANCE);

	/**
	 * Creates an instance of a Netty TCP client which can then be used to
	 * connect to a remote Nadron server. This constructor delegates to
	 * {@link #NettyTCPClient(InetSocketAddress)} constructor after creating a
	 * {@link InetSocketAddress} instance based on the host and port number
	 * passed in.
	 * 
	 * @param nadronHost
	 *            The host name of the remote server on which nadron server is
	 *            running.
	 * @param port
	 *            The port to connect to, on the remote server.
	 */
	public NettyTCPClient(String nadronHost, int port)
	{
		this(new InetSocketAddress(nadronHost, port));
	}

	public NettyTCPClient(final InetSocketAddress serverAddress)
	{
		this(serverAddress, new NioEventLoopGroup(), 5000);
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
	 *            {@link EventLoopGroup} used for client.
	 * @param maxShutdownWaitTime
	 *            The amount of time in seconds to wait for this client to close
	 *            all {@link Channel}s and shutdown gracefully.
	 */
	public NettyTCPClient(final InetSocketAddress serverAddress,
			final EventLoopGroup boss, 
			final int maxShutdownWaitTime)
	{
		this.serverAddress = serverAddress;
		this.boss = boss;
		this.bootstrap = new Bootstrap();
		bootstrap.group(boss).channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_KEEPALIVE, true);
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
				boss.shutdownGracefully();
			}
		});
	}

	/**
	 * This method delegates to the
	 * {@link #connect(ChannelInitializer, Event, int, TimeUnit)} method
	 * internally. It will pass in a default of 5 seconds wait time to the
	 * delegated method.
	 * 
	 * @param pipelineFactory
	 *            The factory used to create a pipeline of decoders and encoders
	 *            for each {@link Channel} that it creates on connection.
	 * @param loginEvent
	 *            The event contains the {@link ByteBuf} to be transmitted
	 *            to nadron server for logging in. Values inside this buffer include
	 *            username, password, connection key, <b>optional</b> local
	 *            address of the UDP channel used by this session.
	 * @return Returns the Netty {@link Channel} which is the connection to the
	 *         remote nadron server.
	 * @throws InterruptedException
	 */
	public Channel connect(final ChannelInitializer<SocketChannel> pipelineFactory,
			final Event loginEvent) throws InterruptedException
	{
		return connect(pipelineFactory, loginEvent, 5, TimeUnit.SECONDS);
	}

	/**
	 * Method that is used to create the connection or {@link Channel} to
	 * communicated with the remote nadron server.
	 * 
	 * @param pipelineFactory
	 *            The factory used to create a pipeline of decoders and encoders
	 *            for each {@link Channel} that it creates on connection.
	 * @param loginEvent
	 *            The event contains the {@link ByteBuf} to be transmitted
	 *            to nadron server for logging in. Values inside this buffer include
	 *            username, password, connection key, <b>optional</b> local
	 *            address of the UDP channel used by this session.
	 * @param timeout
	 *            The amount of time to wait for this connection be created
	 *            successfully.
	 * @param unit
	 *            The unit of timeout SECONDS, MILLISECONDS etc. Default is 5
	 *            seconds.
	 * @return Returns the Netty {@link Channel} which is the connection to the
	 *         remote nadron server.
	 * @throws InterruptedException
	 */
	public Channel connect(final ChannelInitializer<SocketChannel> pipelineFactory,
			final Event loginEvent, int timeout, TimeUnit unit)
			throws InterruptedException
	{
		ChannelFuture future;
		synchronized (bootstrap)
		{
			bootstrap.handler(pipelineFactory);
			future = bootstrap.connect(serverAddress);
			future.addListener(new ChannelFutureListener()
			{
				@Override
				public void operationComplete(ChannelFuture future)
						throws Exception
				{
					if (future.isSuccess())
					{
						future.channel().writeAndFlush(loginEvent);
					}
					else
					{
						future.cause().printStackTrace();
						throw new RuntimeException(future.cause()
								.getMessage());
					}
				}
			});
		}
		return future.channel();
	}

	public InetSocketAddress getServerAddress()
	{
		return serverAddress;
	}

	public EventLoopGroup getBoss()
	{
		return boss;
	}


	public Bootstrap getBootstrap()
	{
		return bootstrap;
	}

	public int getMaxShutdownWaitTime()
	{
		return maxShutdownWaitTime;
	}

}
