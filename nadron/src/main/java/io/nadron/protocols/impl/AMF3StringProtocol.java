package io.nadron.protocols.impl;

import io.nadron.app.PlayerSession;
import io.nadron.handlers.netty.AMF3ToJavaObjectDecoder;
import io.nadron.handlers.netty.JavaObjectToAMF3Encoder;
import io.nadron.handlers.netty.NulEncoder;
import io.nadron.protocols.AbstractNettyProtocol;
import io.nadron.util.NettyUtils;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.base64.Base64Decoder;
import io.netty.handler.codec.base64.Base64Encoder;



/**
 * This protocol defines AMF3 that is base 64 and String encoded sent over the
 * wire. Used by XMLSocket flash clients to send AMF3 data.
 * 
 * @author Abraham Menacherry
 * 
 */
public class AMF3StringProtocol extends AbstractNettyProtocol
{
	/**
	 * The maximum size of the incoming message in bytes. The
	 * {@link DelimiterBasedFrameDecoder} will use this value in order to throw
	 * a {@link TooLongFrameException}.
	 */
	int maxFrameSize;
	/**
	 * The flash client would encode the AMF3 bytes into a base 64 encoded
	 * string, this decoder is used to decode it back.
	 */
	private Base64Decoder base64Decoder;
	/**
	 * Once the game handler is done with its operations, it writes back the
	 * java object to the client. When writing back to flash client, it needs to
	 * use this encoder to encode it to AMF3 format.
	 */
	private JavaObjectToAMF3Encoder javaObjectToAMF3Encoder;
	/**
	 * The flash client expects a AMF3 bytes to be passed in as base 64 encoded
	 * string. This encoder will encode the bytes accordingly.
	 */
	private Base64Encoder base64Encoder;
	/**
	 * Flash client expects a nul byte 0x00 to be added as the end byte of any
	 * communication with it. This encoder will add this nul byte to the end of
	 * the message. Could be considered as a message "footer".
	 */
	private NulEncoder nulEncoder;

	public AMF3StringProtocol()
	{
		super("AMF3_STRING");
	}

	@Override
	public void applyProtocol(PlayerSession playerSession)
	{
		ChannelPipeline pipeline = NettyUtils
				.getPipeLineOfConnection(playerSession);

		// Upstream handlers or encoders (i.e towards server) are added to
		// pipeline now.
		pipeline.addLast("framer", new DelimiterBasedFrameDecoder(maxFrameSize,
				Delimiters.nulDelimiter()));
		pipeline.addLast("base64Decoder", base64Decoder);
		pipeline.addLast("amf3ToJavaObjectDecoder", createAMF3ToJavaObjectDecoder());

		// Downstream handlers - Filter for data which flows from server to
		// client. Note that the last handler added is actually the first
		// handler for outgoing data.
		pipeline.addLast("nulEncoder", nulEncoder);
		pipeline.addLast("base64Encoder", base64Encoder);
		pipeline.addLast("javaObjectToAMF3Encoder", javaObjectToAMF3Encoder);
	}

	protected AMF3ToJavaObjectDecoder createAMF3ToJavaObjectDecoder()
	{
		return new AMF3ToJavaObjectDecoder();
	}
	
	public int getMaxFrameSize()
	{
		return maxFrameSize;
	}

	public void setMaxFrameSize(int frameSize)
	{
		this.maxFrameSize = frameSize;
	}

	public Base64Decoder getBase64Decoder()
	{
		return base64Decoder;
	}

	public void setBase64Decoder(Base64Decoder base64Decoder)
	{
		this.base64Decoder = base64Decoder;
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

	public Base64Encoder getBase64Encoder()
	{
		return base64Encoder;
	}

	public void setBase64Encoder(Base64Encoder base64Encoder)
	{
		this.base64Encoder = base64Encoder;
	}

	public NulEncoder getNulEncoder()
	{
		return nulEncoder;
	}

	public void setNulEncoder(NulEncoder nulEncoder)
	{
		this.nulEncoder = nulEncoder;
	}

}
