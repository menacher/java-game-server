package org.menacheri.jetserver.protocols.impl;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.menacheri.jetserver.app.PlayerSession;
import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.handlers.netty.AMF3ToEventSourceDecoder;
import org.menacheri.jetserver.handlers.netty.DefaultToServerHandler;
import org.menacheri.jetserver.handlers.netty.EventDecoder;
import org.menacheri.jetserver.handlers.netty.EventEncoder;
import org.menacheri.jetserver.handlers.netty.EventSourceToAMF3Encoder;
import org.menacheri.jetserver.protocols.AbstractNettyProtocol;
import org.menacheri.jetserver.util.NettyUtils;


/**
 * This protocol defines AMF3 as a byte array being sent over the wire. Used by
 * flash clients that use Socket class. This class applies the flash AMF3
 * protocol to the {@link PlayerSession}'s pipeline. The major handlers
 * involved are {@link AMF3ToEventSourceDecoder} and
 * {@link EventSourceToAMF3Encoder}.
 * 
 * @author Abraham Menacherry
 * 
 * 
 */
public class AMF3Protocol extends AbstractNettyProtocol
{
	/**
	 * After the frame decoder retrieves the bytes from the incoming stream,
	 * this decoder will convert it to an {@link Event} with the opcode set as
	 * the first byte read from the buffer. And the source object of the event
	 * created will have the rest of the {@link ChannelBuffer}.
	 */
	private EventDecoder eventDecoder;
	
	/**
	 * This decoder will do the actual serialization to java object. Any game
	 * handlers need to be added after this in the pipeline so that they can
	 * operate on the java object.
	 */
	private AMF3ToEventSourceDecoder amf3ToEventSourceDecoder;
	
	/**
	 * Once the game handler is done with its operations, it writes back the
	 * java object to the client. When writing back to flash client, it needs to
	 * use this encoder to encode it to AMF3 format.
	 */
	private EventSourceToAMF3Encoder eventSourceToAMF3Encoder;

	/**
	 * This encoder will take the event parsed by the java object to AMF3
	 * encoder and create a single wrapped {@link ChannelBuffer} with the opcode
	 * as header and amf3 bytes as body.
	 */
	private EventEncoder eventEncoder;
	
	/**
	 * Utility handler provided by netty to add the length of the outgoing
	 * message to the message as a header.
	 */
	private LengthFieldPrepender lengthFieldPrepender;

	public AMF3Protocol()
	{
		super("AMF3");
	}

	@Override
	public void applyProtocol(PlayerSession playerSession)
	{
		ChannelPipeline pipeline = NettyUtils
				.getPipeLineOfConnection(playerSession);

		// Upstream handlers or encoders (i.e towards server) are added to
		// pipeline now.
		pipeline.addLast("lengthDecoder", createLengthBasedFrameDecoder());
		pipeline.addLast("eventDecoder",eventDecoder);
		pipeline.addLast("amf3ToEventSourceDecoder", amf3ToEventSourceDecoder);
		pipeline.addLast("eventHandler", new DefaultToServerHandler(
				playerSession));
		
		// Downstream handlers (i.e towards client) are added to pipeline now.
		// NOTE the last encoder in the pipeline is the first encoder to be called.
		pipeline.addLast("lengthFieldPrepender", lengthFieldPrepender);
		pipeline.addLast("eventEncoder",eventEncoder);
		pipeline.addLast("eventSourceToAMF3Encoder", eventSourceToAMF3Encoder);
	}

	public EventSourceToAMF3Encoder getEventSourceToAMF3Encoder()
	{
		return eventSourceToAMF3Encoder;
	}

	public LengthFieldPrepender getLengthFieldPrepender()
	{
		return lengthFieldPrepender;
	}

	public EventDecoder getEventDecoder()
	{
		return eventDecoder;
	}

	public EventEncoder getEventEncoder()
	{
		return eventEncoder;
	}

	public AMF3ToEventSourceDecoder getAmf3ToEventSourceDecoder()
	{
		return amf3ToEventSourceDecoder;
	}
	
	public void setEventDecoder(EventDecoder eventDecoder)
	{
		this.eventDecoder = eventDecoder;
	}

	public void setEventSourceToAMF3Encoder(
			EventSourceToAMF3Encoder eventToAMF3Encoder)
	{
		this.eventSourceToAMF3Encoder = eventToAMF3Encoder;
	}

	public void setEventEncoder(EventEncoder eventEncoder)
	{
		this.eventEncoder = eventEncoder;
	}

	public void setLengthFieldPrepender(LengthFieldPrepender lengthFieldPrepender)
	{
		this.lengthFieldPrepender = lengthFieldPrepender;
	}

	public void setAmf3ToEventSourceDecoder(
			AMF3ToEventSourceDecoder amf3ToEventSourceDecoder)
	{
		this.amf3ToEventSourceDecoder = amf3ToEventSourceDecoder;
	}

}
