package org.menacheri.jetserver.server.netty;

import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.menacheri.jetserver.concurrent.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlashPolicyServer extends NettyTCPServer
{
	private static final Logger LOG = LoggerFactory.getLogger(FlashPolicyServer.class);
	private int portNumber = 843;
	
	public int getPortNumber(String[] args)
	{
		if (null == args || args.length != 2)
		{
			LOG.debug("Going to use port: {}", portNumber);
			return portNumber;
		}

		try
		{
			int portNumberArg = Integer.parseInt(args[1]);
			LOG.debug("Going to use port: {}", portNumberArg);
			return portNumberArg;
		}
		catch (NumberFormatException e)
		{
			LOG.error("Exception occurred while "
					+ "trying to parse the port number: {}, {}", args[0], e);
			throw e;
		}
	}

	public Bootstrap createServerBootstrap()
	{
		// TODO The thread pools should be injected from spring.
		serverBootstrap = new ServerBootstrap(
				
				new NioServerSocketChannelFactory(Executors
						.newFixedThreadPool(1,new NamedThreadFactory(
								"Flash-Server-Boss")), Executors
						.newFixedThreadPool(1,new NamedThreadFactory(
								"Flash-Server-Worker"))));

		return serverBootstrap;
	}
	
	public int getPortNumber() 
	{
		return portNumber;
	}

	public void setPortNumber(int portNumber) 
	{
		this.portNumber = portNumber;
	}
	
}
