package org.menacheri.protocols.impl;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.menacheri.app.IPlayerSession;
import org.menacheri.event.IEvent;
import org.menacheri.handlers.netty.AMF3ToJavaObjectDecoder;
import org.menacheri.handlers.netty.DefaultToServerHandler;
import org.menacheri.handlers.netty.EventDecoder;
import org.menacheri.handlers.netty.EventEncoder;
import org.menacheri.handlers.netty.JavaObjectToAMF3Encoder;
import org.menacheri.protocols.AbstractNettyProtocol;
import org.menacheri.protocols.ServerDataProtocols;
import org.menacheri.util.NettyUtils;


/**
 * This protocol defines AMF3 as a byte array being sent over the wire. Used by
 * flash clients that use Socket class. This class applies the flash AMF3
 * protocol to the {@link IPlayerSession}'s pipeline. The major handlers
 * involved are {@link AMF3ToJavaObjectDecoder} and
 * {@link JavaObjectToAMF3Encoder}.
 * 
 * @author Abraham Menacherry
 * 
 * 
 */
public class AMF3Protocol extends AbstractNettyProtocol
{
	/**
	 * After the frame decoder retrieves the bytes from the incoming stream,
	 * this decoder will convert it to an {@link IEvent} with the opCode set as
	 * the first byte read from the buffer. And the source object of the event
	 * created will have the rest of the {@link ChannelBuffer}.
	 */
	private EventDecoder eventDecoder;
	
	/**
	 * This decoder will do the actual serialization to java object. Any game
	 * handlers need to be added after this in the pipeline so that they can
	 * operate on the java object.
	 */
	private AMF3ToJavaObjectDecoder amf3ToJavaObjectDecoder;
	
	/**
	 * Once the game handler is done with its operations, it writes back the
	 * java object to the client. When writing back to flash client, it needs to
	 * use this encoder to encode it to AMF3 format.
	 */
	private JavaObjectToAMF3Encoder javaObjectToAMF3Encoder;

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
		super("" + ServerDataProtocols.AMF3, ServerDataProtocols.AMF3);
	}

	@Override
	public void applyProtocol(IPlayerSession playerSession)
	{
		ChannelPipeline pipeline = NettyUtils
				.getPipeLineOfConnection(playerSession);

		// Upstream handlers or encoders (i.e towards server) are added to
		// pipeline now.
		pipeline.addLast("lengthDecoder", createLengthBasedFrameDecoder());
		pipeline.addLast("eventDecoder",eventDecoder);
		pipeline.addLast("amf3ToJavaObjectDecoder", amf3ToJavaObjectDecoder);
		pipeline.addLast("eventHandler", new DefaultToServerHandler(
				playerSession));
		
		// Downstream handlers (i.e towards client) are added to pipeline now.
		// NOTE the last encoder in the pipeline is the first encoder to be called.
		pipeline.addLast("lengthFieldPrepender", lengthFieldPrepender);
		pipeline.addLast("eventEncoder",eventEncoder);
		pipeline.addLast("javaObjectToAMF3Encoder", javaObjectToAMF3Encoder);
	}

	public AMF3ToJavaObjectDecoder getAmf3ToJavaObjectDecoder()
	{
		return amf3ToJavaObjectDecoder;
	}

	public JavaObjectToAMF3Encoder getJavaObjectToAMF3Encoder()
	{
		return javaObjectToAMF3Encoder;
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

	public void setEventDecoder(EventDecoder eventDecoder)
	{
		this.eventDecoder = eventDecoder;
	}

	public void setAmf3ToJavaObjectDecoder(
			AMF3ToJavaObjectDecoder amf3ToJavaObjectDecoder)
	{
		this.amf3ToJavaObjectDecoder = amf3ToJavaObjectDecoder;
	}

	public void setJavaObjectToAMF3Encoder(
			JavaObjectToAMF3Encoder javaObjectToAMF3Encoder)
	{
		this.javaObjectToAMF3Encoder = javaObjectToAMF3Encoder;
	}

	public void setEventEncoder(EventEncoder eventEncoder)
	{
		this.eventEncoder = eventEncoder;
	}

	public void setLengthFieldPrepender(LengthFieldPrepender lengthFieldPrepender)
	{
		this.lengthFieldPrepender = lengthFieldPrepender;
	}

}
