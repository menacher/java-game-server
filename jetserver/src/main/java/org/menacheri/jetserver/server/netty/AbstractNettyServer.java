package org.menacheri.jetserver.server.netty;

import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.menacheri.jetserver.app.Session;
import org.menacheri.jetserver.service.GameAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


public abstract class AbstractNettyServer implements NettyServer
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractNettyServer.class);
	public static final ChannelGroup ALL_CHANNELS = new DefaultChannelGroup("JETSERVER-CHANNELS");
	protected Session session;
	protected InetSocketAddress socketAddress;
	protected int portNumber = 18090;
	protected Bootstrap serverBootstrap;
	protected ChannelPipelineFactory pipelineFactory;
	protected GameAdminService gameAdminService;
	
	public AbstractNettyServer()
	{
		super();
	}

	@Override
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
		serverBootstrap.releaseExternalResources();
		gameAdminService.shutdown();
	}
	
	@Override
	public void configureServerBootStrap(String[] optionsList)
	{
		// For clients who do not use spring.
		if(null == serverBootstrap){
			createServerBootstrap();
		}
		serverBootstrap.setPipelineFactory(pipelineFactory);
		if (null != optionsList && optionsList.length > 0)
		{
			for (String option : optionsList)
			{
				serverBootstrap.setOption(option, true);
			}
		}
	}

	public int getPortNumber(String[] args)
	{
		if (null == args || args.length < 1)
		{
			return portNumber;
		}

		try
		{
			return Integer.parseInt(args[0]);
		}
		catch (NumberFormatException e)
		{
			LOG.error("Exception occurred while "
					+ "trying to parse the port number: {}", args[0]);
			LOG.error("NumberFormatException: {}",e);
			throw e;
		}
	}
	
	@Override
	public Bootstrap getServerBootstrap()
	{
		return serverBootstrap;
	}

	@Override
	public void setServerBootstrap(Bootstrap serverBootstrap)
	{
		this.serverBootstrap = serverBootstrap;
	}
	
	@Override
	public ChannelPipelineFactory getPipelineFactory()
	{
		return pipelineFactory;
	}

	@Override
	@Required
	public void setPipelineFactory(ChannelPipelineFactory factory)
	{
		pipelineFactory = factory;
	}

	public int getPortNumber()
	{
		return portNumber;
	}

	public void setPortNumber(int portNumber)
	{
		this.portNumber = portNumber;
	}

	public GameAdminService getGameAdminService()
	{
		return gameAdminService;
	}

	public void setGameAdminService(GameAdminService gameAdminService)
	{
		this.gameAdminService = gameAdminService;
	}

	@Override
	public InetSocketAddress getSocketAddress()
	{
		return socketAddress;
	}

	public void setInetAddress(InetSocketAddress inetAddress)
	{
		this.socketAddress = inetAddress;
	}

	@Override
	public String toString()
	{
		return "NettyServer [socketAddress=" + socketAddress + ", portNumber="
				+ portNumber + "]";
	}

	@Override
	public Session getSession()
	{
		return session;
	}

	@Override
	public void setSession(Session session)
	{
		this.session = session;
	}

}
