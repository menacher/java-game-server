package io.nadron.server.netty;

import io.nadron.service.GameAdminService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractNettyServer implements NettyServer
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractNettyServer.class);
	public static final ChannelGroup ALL_CHANNELS = new DefaultChannelGroup("NADRON-CHANNELS", GlobalEventExecutor.INSTANCE);
	protected GameAdminService gameAdminService;
	protected final NettyConfig nettyConfig;
	protected ChannelInitializer<? extends Channel> channelInitializer;
	
	public AbstractNettyServer(NettyConfig nettyConfig,
			ChannelInitializer<? extends Channel> channelInitializer) 
	{
		this.nettyConfig = nettyConfig;
		this.channelInitializer = channelInitializer;
	}

	@Override
	public void startServer(int port) throws Exception 
	{
		nettyConfig.setPortNumber(port);
		nettyConfig.setSocketAddress(new InetSocketAddress(port));
		startServer();
	}
	
	@Override
	public void startServer(InetSocketAddress socketAddress) throws Exception
	{
		nettyConfig.setSocketAddress(socketAddress);
		startServer();
	}
	
	@Override
	public void stopServer() throws Exception 
	{
		LOG.debug("In stopServer method of class: {}", this.getClass()
				.getName());
		ChannelGroupFuture future = ALL_CHANNELS.close();
		try 
		{
			future.await();
		} 
		catch (InterruptedException e) 
		{
			LOG.error(
					"Execption occurred while waiting for channels to close: {}",
					e);
		} 
		finally 
		{
			// TODO move this part to spring.
			if (null != nettyConfig.getBossGroup()) 
			{
				nettyConfig.getBossGroup().shutdownGracefully();
			}
			if (null != nettyConfig.getWorkerGroup()) 
			{
				nettyConfig.getWorkerGroup().shutdownGracefully();
			}
			gameAdminService.shutdown();
		}
	}
	
	@Override
	public ChannelInitializer<? extends Channel> getChannelInitializer()
	{
		return channelInitializer;
	}

	@Override
	public NettyConfig getNettyConfig() {
		return nettyConfig;
	}

	protected EventLoopGroup getBossGroup(){
		return nettyConfig.getBossGroup();
	}
	
	protected EventLoopGroup getWorkerGroup(){
		return nettyConfig.getWorkerGroup();
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
		return nettyConfig.getSocketAddress();
	}

	@Override
	public String toString() 
	{
		return "NettyServer [socketAddress=" + nettyConfig.getSocketAddress()
				+ ", portNumber=" + nettyConfig.getPortNumber() + "]";
	}
	
}
