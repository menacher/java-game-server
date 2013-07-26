package io.nadron.protocols.impl;

import io.nadron.app.PlayerSession;
import io.nadron.handlers.netty.AMF3ToJavaObjectDecoder;
import io.nadron.handlers.netty.DefaultToServerHandler;
import io.nadron.handlers.netty.JavaObjectToAMF3Encoder;
import io.nadron.protocols.AbstractNettyProtocol;
import io.nadron.util.NettyUtils;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldPrepender;



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
		pipeline.addLast("amf3ToJavaObjectDecoder", createAMF3ToJavaObjectDecoder());
		pipeline.addLast("eventHandler", new DefaultToServerHandler(
				playerSession));
		
		// Downstream handlers (i.e towards client) are added to pipeline now.
		// NOTE the last encoder in the pipeline is the first encoder to be called.
		pipeline.addLast("lengthFieldPrepender", lengthFieldPrepender);
		pipeline.addLast("javaObjectToAMF3Encoder", javaObjectToAMF3Encoder);
	}

	protected AMF3ToJavaObjectDecoder createAMF3ToJavaObjectDecoder()
	{
		return new AMF3ToJavaObjectDecoder();
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
