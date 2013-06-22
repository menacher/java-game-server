package io.nadron.protocols.impl;

import io.nadron.app.PlayerSession;
import io.nadron.handlers.netty.DefaultToServerHandler;
import io.nadron.handlers.netty.EventDecoder;
import io.nadron.handlers.netty.EventEncoder;
import io.nadron.protocols.AbstractNettyProtocol;
import io.nadron.util.NettyUtils;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldPrepender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A simple protocol which would just strip out the length bytes from the buffer
 * and return the <a href="http://www.jboss.org/netty">Netty</a> ChannelBuffer
 * to the next decoder or game handler in the {@link ChannelPipeline}. For out
 * going messages it will set the length using the {@link LengthFieldPrepender}
 * encoder. The binary packets it receives will be of the format
 * [OPCODE][LENGTH][PAYLOAD] will find this protocol the most useful.
 * 
 * @author Abraham Menacherry
 * 
 */
public class ChannelBufferProtocol extends AbstractNettyProtocol
{
	private static final Logger LOG = LoggerFactory.getLogger(ChannelBufferProtocol.class);
	/**
	 * Utility handler provided by netty to add the length of the outgoing
	 * message to the message as a header.
	 */
	private LengthFieldPrepender lengthFieldPrepender;
	private EventDecoder eventDecoder;
	private EventEncoder eventEncoder;
	
	public ChannelBufferProtocol()
	{
		super("CHANNEL_BUFFER_PROTOCOL");
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
		pipeline.addLast("eventDecoder",eventDecoder);
		pipeline.addLast("eventHandler", new DefaultToServerHandler(
				playerSession));

		// Downstream handlers - Filter for data which flows from server to
		// client. Note that the last handler added is actually the first
		// handler for outgoing data.
		pipeline.addLast("lengthFieldPrepender", lengthFieldPrepender);
		pipeline.addLast("eventEncoder",eventEncoder);
	}

	public LengthFieldPrepender getLengthFieldPrepender()
	{
		return lengthFieldPrepender;
	}

	public void setLengthFieldPrepender(LengthFieldPrepender lengthFieldPrepender)
	{
		this.lengthFieldPrepender = lengthFieldPrepender;
	}

	public EventDecoder getEventDecoder()
	{
		return eventDecoder;
	}

	public void setEventDecoder(EventDecoder eventDecoder)
	{
		this.eventDecoder = eventDecoder;
	}

	public EventEncoder getEventEncoder()
	{
		return eventEncoder;
	}

	public void setEventEncoder(EventEncoder eventEncoder)
	{
		this.eventEncoder = eventEncoder;
	}

}
