package io.nadron.client.protocol.impl;

import io.nadron.client.app.Session;
import io.nadron.client.handlers.netty.DefaultToClientHandler;
import io.nadron.client.handlers.netty.EventObjectDecoder;
import io.nadron.client.handlers.netty.EventObjectEncoder;
import io.nadron.client.protocol.Protocol;
import io.nadron.client.util.NettyUtils;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class NettyObjectProtocol implements Protocol{

	public static final NettyObjectProtocol INSTANCE = new NettyObjectProtocol();
	
	@Override
	public void applyProtocol(Session session) {
		ChannelPipeline pipeline = NettyUtils.getPipeLineOfSession(session);
		NettyUtils.clearPipeline(pipeline);
		pipeline.addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(
				Integer.MAX_VALUE, 0, 2, 0, 2));
		pipeline.addLast("eventDecoder", new EventObjectDecoder());
		pipeline.addLast(new DefaultToClientHandler(session));
		pipeline.addLast("lengthFieldPrepender", new LengthFieldPrepender(
				2));
		pipeline.addLast("eventEncoder", new EventObjectEncoder());
	}

	

	
}
