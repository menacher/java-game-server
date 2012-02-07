package org.menacheri.server.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.menacheri.concurrent.NamedThreadFactory;
import org.menacheri.service.IGameAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is used for TCP IP communications with client. It uses Netty tcp
 * server bootstrap for this.
 * 
 * @author Abraham Menacherry
 * 
 */
public class NettyTCPServer extends NettyServer
{
	private static final Logger LOG = LoggerFactory.getLogger(NettyTCPServer.class);
	public NettyTCPServer()
	{

	}

	public NettyTCPServer(int portNumber, ServerBootstrap serverBootstrap,
			ChannelPipelineFactory pipelineFactory,
			IGameAdminService gameAdminService)
	{
		super(portNumber, serverBootstrap, pipelineFactory, gameAdminService);
	}

	public void startServer(int port)
	{
		portNumber = port;
		startServer(null);
	}
	
	public void startServer()
	{
		startServer(null);
	}
	
	public boolean startServer(String[] args)
	{
		if (null == args || args.length == 0)
		{
			String[] optionsList = new String[2];
			optionsList[0] = "child.tcpNoDelay";
			optionsList[1] = "child.keepAlive";
			configureServerBootStrap(optionsList);
		}
		else
		{
			configureServerBootStrap(args);
		}
		int portNumber = getPortNumber(args);
		try
		{
			((ServerBootstrap) serverBootstrap).bind(new InetSocketAddress(
					portNumber));
		}
		catch (ChannelException e)
		{
			LOG.error("Unable to start TCP server due to error {}",e);
			return false;
		}
		return true;
	}

	public Bootstrap createServerBootstrap()
	{
		// TODO The thread pools should be injected from spring.
		serverBootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(Executors
						.newCachedThreadPool(new NamedThreadFactory(
								"TCP-Server-Boss")), Executors
						.newCachedThreadPool(new NamedThreadFactory(
								"TCP-Server-Worker"))));

		return serverBootstrap;
	}

}
