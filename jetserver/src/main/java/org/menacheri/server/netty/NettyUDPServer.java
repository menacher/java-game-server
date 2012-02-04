package org.menacheri.server.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.FixedReceiveBufferSizePredictorFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.menacheri.concurrent.NamedThreadFactory;
import org.menacheri.service.IGameAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This server does UDP connection less broadcast. Since it does not store the
 * connection, each call to a channel write must also contain the remote socket
 * address <code>e.getChannel().write("Message", e.getRemoteAddress())</code>.
 * Since it uses the same channel for all incoming connections, the handlers
 * cannot be modified refer to <a
 * href="http://www.jboss.org/netty/community.html#nabble-f685700">nabble
 * post</a>
 * 
 * @author Abraham Menacherry
 * 
 */
public class NettyUDPServer extends NettyServer
{
	private static final Logger LOG = LoggerFactory.getLogger(NettyUDPServer.class);
	private FixedReceiveBufferSizePredictorFactory bufferSizePredictor;
	/**
	 * The connected channel for this server. This reference can be used to
	 * shutdown this server.
	 */
	private Channel channel;
	
	public NettyUDPServer()
	{

	}

	public NettyUDPServer(int portNumber, ServerBootstrap serverBootstrap,
			ChannelPipelineFactory pipelineFactory,
			IGameAdminService gameAdminService)
	{
		super(portNumber, serverBootstrap, pipelineFactory, gameAdminService);
	}

	@Override
	public void startServer(int port)
	{
		portNumber = port;
		startServer(null);
	}
	
	public boolean startServer(String[] args)
	{
		serverBootstrap.setOption("broadcast", "false");
		serverBootstrap.setOption("receiveBufferSizePredictorFactory",
				bufferSizePredictor);
		serverBootstrap.setOption("sendBufferSize", 65536);
		serverBootstrap.setOption("receiveBufferSize", 65536);
		configureServerBootStrap(args);

		int portNumber = getPortNumber(args);
		try
		{
			channel = ((ConnectionlessBootstrap) serverBootstrap)
					.bind(new InetSocketAddress(portNumber));
		}
		catch (ChannelException e)
		{
			LOG.error("Unable to start UDP server due to error {}",e);
			return false;
		}
		return true;
	}

	@Override
	public Bootstrap createServerBootstrap()
	{
		serverBootstrap = new ConnectionlessBootstrap(
				new NioDatagramChannelFactory(Executors
						.newCachedThreadPool(new NamedThreadFactory(
								"UDP-Server-Worker"))));
		return serverBootstrap;
	}

	@Override
	public boolean stopServer()
	{
		LOG.debug("In stopServer method of class: {}",
				this.getClass().getName());
		serverBootstrap.releaseExternalResources();
		if(null != channel)
		{
			channel.close();
		}
		gameAdminService.shutdown();
		return true;
	}
	
	public FixedReceiveBufferSizePredictorFactory getBufferSizePredictor()
	{
		return bufferSizePredictor;
	}

	public void setBufferSizePredictor(
			FixedReceiveBufferSizePredictorFactory bufferSizePredictor)
	{
		this.bufferSizePredictor = bufferSizePredictor;
	}

}
