package io.nadron.server.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class FlashPolicyServer extends NettyTCPServer {
	
	public FlashPolicyServer(NettyConfig nettyConfig, ChannelInitializer<? extends Channel> channelInitializer) 
	{
		super(nettyConfig, channelInitializer);
		if (nettyConfig.getPortNumber() == 0
				|| nettyConfig.getPortNumber() == 18090) 
		{
			nettyConfig.setPortNumber(843);
		}
	}

}
