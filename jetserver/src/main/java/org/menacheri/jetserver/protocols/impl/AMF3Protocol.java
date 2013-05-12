package org.menacheri.jetserver.protocols.impl;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldPrepender;

import org.menacheri.jetserver.app.PlayerSession;
import org.menacheri.jetserver.handlers.netty.AMF3ToJavaObjectDecoder;
import org.menacheri.jetserver.handlers.netty.DefaultToServerHandler;
import org.menacheri.jetserver.handlers.netty.JavaObjectToAMF3Encoder;
import org.menacheri.jetserver.protocols.AbstractNettyProtocol;
import org.menacheri.jetserver.util.NettyUtils;


/**
 * This protocol defines AMF3 as a byte array being sent over the wire. Used by
 * flash clients that use Socket class. This class applies the flash AMF3
 * protocol to the {@link PlayerSession}'s pipeline.
 * 
 * @author Abraham Menacherry
 * 
 * 
 */
public class AMF3Protocol extends AbstractNettyProtocol
{
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
		pipeline.addLast("amf3ToJavaObjectDecoder", amf3ToJavaObjectDecoder);
		pipeline.addLast("eventHandler", new DefaultToServerHandler(
				playerSession));
		
		// Downstream handlers (i.e towards client) are added to pipeline now.
		// NOTE the last encoder in the pipeline is the first encoder to be called.
		pipeline.addLast("lengthFieldPrepender", lengthFieldPrepender);
		pipeline.addLast("javaObjectToAMF3Encoder", javaObjectToAMF3Encoder);
	}

	public AMF3ToJavaObjectDecoder getAmf3ToJavaObjectDecoder() 
	{
		return amf3ToJavaObjectDecoder;
	}

	public void setAmf3ToJavaObjectDecoder(
			AMF3ToJavaObjectDecoder amf3ToJavaObjectDecoder) 
	{
		this.amf3ToJavaObjectDecoder = amf3ToJavaObjectDecoder;
	}

	public JavaObjectToAMF3Encoder getJavaObjectToAMF3Encoder() 
	{
		return javaObjectToAMF3Encoder;
	}

	public void setJavaObjectToAMF3Encoder(
			JavaObjectToAMF3Encoder javaObjectToAMF3Encoder) 
	{
		this.javaObjectToAMF3Encoder = javaObjectToAMF3Encoder;
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
