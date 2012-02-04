package org.menacheri.server.netty;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.menacheri.service.IGameAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


public abstract class NettyServer implements INettyServerManager
{
	private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);
	
	int portNumber = 8090;
	Bootstrap serverBootstrap;
	ChannelPipelineFactory pipelineFactory;
	IGameAdminService gameAdminService;
	
	public NettyServer()
	{
		super();
	}

	public NettyServer(int portNumber, ServerBootstrap serverBootstrap,
			ChannelPipelineFactory pipelineFactory,IGameAdminService gameAdminService)
	{
		super();
		this.portNumber = portNumber;
		this.serverBootstrap = serverBootstrap;
		this.pipelineFactory = pipelineFactory;
		this.gameAdminService = gameAdminService;
	}
	
	public abstract void startServer(int port);
	
	
	public boolean stopServer()
	{
		LOG.debug("In stopServer method of class: {}",
				this.getClass().getName());
		serverBootstrap.releaseExternalResources();
		gameAdminService.shutdown();
		return true;
	}

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
				serverBootstrap.setOption(option, Boolean.valueOf(true));
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

	public IGameAdminService getGameAdminService()
	{
		return gameAdminService;
	}

	public void setGameAdminService(IGameAdminService gameAdminService)
	{
		this.gameAdminService = gameAdminService;
	}


}
