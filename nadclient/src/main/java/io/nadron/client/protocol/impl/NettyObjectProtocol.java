package io.nadron.client.protocol.impl;

import io.nadron.client.app.Session;
import io.nadron.client.handlers.netty.DefaultToClientHandler;
import io.nadron.client.protocol.Protocol;
import io.nadron.client.util.NettyUtils;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyObjectProtocol implements Protocol{

	public static final NettyObjectProtocol INSTANCE = new NettyObjectProtocol();
	
	@Override
	public void applyProtocol(Session session) {
		ChannelPipeline pipeline = NettyUtils.getPipeLineOfSession(session);
		NettyUtils.clearPipeline(pipeline);
		pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
		pipeline.addLast(new ObjectEncoder());
		pipeline.addLast(new DefaultToClientHandler(session));
	}

	

	
}
