package org.menacheri.jetserver.server.netty;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.menacheri.jetserver.concurrent.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is used for TCP IP communications with client. It uses Netty tcp
 * server bootstrap for this.
 * 
 * @author Abraham Menacherry
 * 
 */
public class NettyTCPServer extends AbstractNettyServer
{
	private static final Logger LOG = LoggerFactory.getLogger(NettyTCPServer.class);
	
	private String[] args;
	
	public NettyTCPServer()
	{

	}

	public void startServer(int port) throws Exception
	{
		portNumber = port;
		startServer(args);
	}
	
	@Override
	public void startServer() throws Exception
	{
		startServer(args);
	}
	
	public void startServer(String[] args) throws Exception
	{
		int portNumber = getPortNumber(args);
		InetSocketAddress socketAddress = new InetSocketAddress(portNumber);
		startServer(socketAddress);
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

	@Override
	public TransmissionProtocol getTransmissionProtocol()
	{
		return TRANSMISSION_PROTOCOL.TCP;
	}

	@Override
	public void startServer(InetSocketAddress socketAddress)
	{
		this.socketAddress = socketAddress;
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
		try
		{
			((ServerBootstrap) serverBootstrap).bind(socketAddress);
		}
		catch (ChannelException e)
		{
			LOG.error("Unable to start TCP server due to error {}",e);
			throw e;
		}
	}

	public void stopServer() throws Exception
	{
		LOG.debug("In stopServer method of class: {}",
				this.getClass().getName());
		ChannelGroupFuture future = ALL_CHANNELS.close();
		try {
			future.await();
		} catch (InterruptedException e) {
			LOG.error("Execption occurred while waiting for channels to close: {}",e);
		}
		super.stopServer();
	}
	
	public String[] getArgs()
	{
		return args;
	}

	public void setArgs(String[] args)
	{
		this.args = args;
	}

	@Override
	public String toString()
	{
		return "NettyTCPServer [args=" + Arrays.toString(args)
				+ ", socketAddress=" + socketAddress + ", portNumber=" + portNumber
				+ "]";
	}
	
}
