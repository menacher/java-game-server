package io.nadron.protocols.impl;

import io.nadron.app.PlayerSession;
import io.nadron.handlers.netty.DefaultToServerHandler;
import io.nadron.handlers.netty.EventObjectDecoder;
import io.nadron.handlers.netty.EventObjectEncoder;
import io.nadron.protocols.AbstractNettyProtocol;
import io.nadron.util.NettyUtils;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldPrepender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyObjectProtocol extends AbstractNettyProtocol {

	private static final Logger LOG = LoggerFactory.getLogger(NettyObjectProtocol.class);
	
	private LengthFieldPrepender lengthFieldPrepender;
	
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
		
		// Upstream handlers or encoders (i.e towards server) are added to
		// pipeline now.
		pipeline.addLast("lengthDecoder", createLengthBasedFrameDecoder());
		pipeline.addLast("eventDecoder",  new EventObjectDecoder());
		pipeline.addLast("eventHandler",new DefaultToServerHandler(playerSession));
		
		// Downstream handlers - Filter for data which flows from server to
		// client. Note that the last handler added is actually the first
		// handler for outgoing data.
		pipeline.addLast("lengthFieldPrepender", lengthFieldPrepender);
		pipeline.addLast("eventEncoder", new EventObjectEncoder());
	}

	public LengthFieldPrepender getLengthFieldPrepender() 
	{
		return lengthFieldPrepender;
	}

	public void setLengthFieldPrepender(LengthFieldPrepender lengthFieldPrepender) 
	{
		this.lengthFieldPrepender = lengthFieldPrepender;
	}

}
