package org.menacheri.protocols.impl;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.menacheri.app.IPlayerSession;
import org.menacheri.handlers.netty.DefaultToServerHandler;
import org.menacheri.handlers.netty.MessageBufferEventDecoder;
import org.menacheri.handlers.netty.MessageBufferEventEncoder;
import org.menacheri.protocols.AbstractNettyProtocol;
import org.menacheri.protocols.ServerDataProtocols;
import org.menacheri.util.NettyUtils;
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
		super("" + ServerDataProtocols.CHANNEL_BUFFER_PROTOCOL,
				ServerDataProtocols.CHANNEL_BUFFER_PROTOCOL);
	}
	
	@Override
	public void applyProtocol(IPlayerSession playerSession)
	{
		LOG.trace("Going to apply protocol on session: {}" ,playerSession);
		
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
