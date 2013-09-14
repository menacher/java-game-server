package io.nadron.protocols.impl;

import io.nadron.app.PlayerSession;
import io.nadron.handlers.netty.DefaultToServerHandler;
import io.nadron.protocols.AbstractNettyProtocol;
import io.nadron.util.NettyUtils;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyObjectProtocol extends AbstractNettyProtocol {

	private static final Logger LOG = LoggerFactory.getLogger(MessageBufferProtocol.class);
	
	public NettyObjectProtocol()
	{
		super("NETTY_OBJECT_PROTOCOL");
	}
	
	@Override
	public void applyProtocol(PlayerSession playerSession) {
		LOG.trace("Going to apply {} on session: {}", getProtocolName(),
				playerSession);
		ChannelPipeline pipeline = NettyUtils.getPipeLineOfConnection(playerSession);
		NettyUtils.clearPipeline(pipeline);
		pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
		pipeline.addLast(new ObjectEncoder());
		pipeline.addLast(new DefaultToServerHandler(playerSession));
	}

}
