package io.nadron.server.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.util.Map;
import java.util.Set;

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
public class NettyUDPServer extends AbstractNettyServer 
{
	private static final Logger LOG = LoggerFactory
			.getLogger(NettyUDPServer.class);
	private Bootstrap bootstrap;
	
	public NettyUDPServer(NettyConfig nettyConfig, ChannelInitializer<? extends Channel> channelInitializer) 
	{
		super(nettyConfig, channelInitializer);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void startServer() throws Exception 
	{
		try 
		{
			bootstrap = new Bootstrap();
			Map<ChannelOption<?>, Object> channelOptions = nettyConfig
					.getChannelOptions();
			if (null != channelOptions) 
			{
				Set<ChannelOption<?>> keySet = channelOptions.keySet();
				for (@SuppressWarnings("rawtypes") ChannelOption option : keySet) 
				{
					bootstrap.option(option, channelOptions.get(option));
				}
			}
			Channel channel = bootstrap.group(getBossGroup())
					.channel(NioDatagramChannel.class)
					.handler(getChannelInitializer())
					.bind(nettyConfig.getSocketAddress()).channel();
			ALL_CHANNELS.add(channel);
		} 
		catch (Exception e) 
		{
			LOG.error("UDP Server start error {}, going to shut down", e);
			super.stopServer();
			throw e;
		}
	}

	@Override
	public TransmissionProtocol getTransmissionProtocol() 
	{
		return TRANSMISSION_PROTOCOL.UDP;
	}

	@Override
	public String toString() 
	{
		return "NettyUDPServer [socketAddress=" + nettyConfig.getSocketAddress()
				+ ", portNumber=" + nettyConfig.getPortNumber() + "]";
	}

	@Override
	public void setChannelInitializer(
			ChannelInitializer<? extends Channel> initializer) 
	{
		this.channelInitializer = initializer;
		bootstrap.handler(initializer);
	}

}
