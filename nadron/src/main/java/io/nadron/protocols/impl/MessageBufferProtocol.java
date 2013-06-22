package io.nadron.protocols.impl;

import io.nadron.app.PlayerSession;
import io.nadron.handlers.netty.DefaultToServerHandler;
import io.nadron.handlers.netty.MessageBufferEventDecoder;
import io.nadron.handlers.netty.MessageBufferEventEncoder;
import io.nadron.protocols.AbstractNettyProtocol;
import io.nadron.util.NettyUtils;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldPrepender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MessageBufferProtocol extends AbstractNettyProtocol
{
	private static final Logger LOG = LoggerFactory.getLogger(MessageBufferProtocol.class);
	/**
	 * Utility handler provided by netty to add the length of the outgoing
	 * message to the message as a header.
	 */
	private LengthFieldPrepender lengthFieldPrepender;
	private MessageBufferEventDecoder messageBufferEventDecoder;
	private MessageBufferEventEncoder messageBufferEventEncoder;
	
	public MessageBufferProtocol()
	{
		super("MESSAGE_BUFFER_PROTOCOL");
	}
	
	@Override
	public void applyProtocol(PlayerSession playerSession)
	{
		LOG.trace("Going to apply {} on session: {}", getProtocolName(),
				playerSession);
		
		ChannelPipeline pipeline = NettyUtils
				.getPipeLineOfConnection(playerSession);
		// Upstream handlers or encoders (i.e towards server) are added to
		// pipeline now.
		pipeline.addLast("lengthDecoder", createLengthBasedFrameDecoder());
		pipeline.addLast("messageBufferEventDecoder",messageBufferEventDecoder);
		pipeline.addLast("eventHandler", new DefaultToServerHandler(
				playerSession));

		// Downstream handlers - Filter for data which flows from server to
		// client. Note that the last handler added is actually the first
		// handler for outgoing data.
		pipeline.addLast("lengthFieldPrepender", lengthFieldPrepender);
		pipeline.addLast("messageBufferEventEncoder",messageBufferEventEncoder);

	}

	public LengthFieldPrepender getLengthFieldPrepender()
	{
		return lengthFieldPrepender;
	}

	public void setLengthFieldPrepender(LengthFieldPrepender lengthFieldPrepender)
	{
		this.lengthFieldPrepender = lengthFieldPrepender;
	}
	
	public MessageBufferEventDecoder getMessageBufferEventDecoder()
	{
		return messageBufferEventDecoder;
	}

	public void setMessageBufferEventDecoder(
			MessageBufferEventDecoder messageBufferEventDecoder)
	{
		this.messageBufferEventDecoder = messageBufferEventDecoder;
	}

	public MessageBufferEventEncoder getMessageBufferEventEncoder()
	{
		return messageBufferEventEncoder;
	}

	public void setMessageBufferEventEncoder(
			MessageBufferEventEncoder messageBufferEventEncoder)
	{
		this.messageBufferEventEncoder = messageBufferEventEncoder;
	}

}
