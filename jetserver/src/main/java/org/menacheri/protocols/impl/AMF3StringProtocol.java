package org.menacheri.protocols.impl;

import java.io.ByteArrayInputStream;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.base64.Base64Decoder;
import org.jboss.netty.handler.codec.base64.Base64Encoder;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.menacheri.app.IPlayerSession;
import org.menacheri.handlers.netty.AMF3ToJavaObjectDecoder;
import org.menacheri.handlers.netty.ByteArrayStreamDecoder;
import org.menacheri.handlers.netty.JavaObjectToAMF3Encoder;
import org.menacheri.handlers.netty.NulEncoder;
import org.menacheri.protocols.AbstractNettyProtocol;
import org.menacheri.protocols.ServerDataProtocols;
import org.menacheri.util.NettyUtils;
import org.springframework.beans.factory.annotation.Required;


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
	int frameSize;
	/**
	 * The flash client would encode the AMF3 bytes into a base 64 encoded
	 * string, this decoder is used to decode it back.
	 */
	private Base64Decoder base64Decoder;
	/**
	 * After the frame decoder retrieves the bytes from the incoming stream,
	 * this decoder will convert it to a {@link ByteArrayInputStream} object
	 * which is provided as input to the {@link AMF3ToJavaObjectDecoder}. The
	 * game can add more handlers at this point to do business logic and write
	 * back to the pipeline.
	 */
	private ByteArrayStreamDecoder byteArrayStreamDecoder;
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
		super("" + ServerDataProtocols.AMF3_STRING,
				ServerDataProtocols.AMF3_STRING);
	}

	public AMF3StringProtocol(int frameSize, Base64Decoder base64Decoder,
			ByteArrayStreamDecoder byteArrayStreamDecoder,
			AMF3ToJavaObjectDecoder amf3ToJavaObjectDecoder,
			JavaObjectToAMF3Encoder javaObjectToAMF3Encoder,
			Base64Encoder base64Encoder, NulEncoder nulEncoder)
	{
		super("" + ServerDataProtocols.AMF3_STRING,
				ServerDataProtocols.AMF3_STRING);
		this.frameSize = frameSize;
		this.base64Decoder = base64Decoder;
		this.byteArrayStreamDecoder = byteArrayStreamDecoder;
		this.amf3ToJavaObjectDecoder = amf3ToJavaObjectDecoder;
		this.javaObjectToAMF3Encoder = javaObjectToAMF3Encoder;
		this.base64Encoder = base64Encoder;
		this.nulEncoder = nulEncoder;
	}

	@Override
	public void applyProtocol(IPlayerSession playerSession)
	{
		ChannelPipeline pipeline = NettyUtils
				.getPipeLineOfConnection(playerSession);

		// Upstream handlers or encoders (i.e towards server) are added to
		// pipeline now.
		pipeline.addLast("framer", new DelimiterBasedFrameDecoder(frameSize,
				Delimiters.nulDelimiter()));
		pipeline.addLast("base64Decoder", base64Decoder);
		pipeline.addLast("byteArrayStreamDecoder", byteArrayStreamDecoder);
		pipeline.addLast("amf3ToJavaObjectDecoder", amf3ToJavaObjectDecoder);

		// Downstream handlers - Filter for data which flows from server to
		// client. Note that the last handler added is actually the first
		// handler for outgoing data.
		pipeline.addLast("nulEncoder", nulEncoder);
		pipeline.addLast("base64Encoder", base64Encoder);
		pipeline.addLast("javaObjectToAMF3Encoder", javaObjectToAMF3Encoder);
	}

	public int getFrameSize()
	{
		return frameSize;
	}

	@Required
	public void setFrameSize(int frameSize)
	{
		this.frameSize = frameSize;
	}

	public Base64Decoder getBase64Decoder()
	{
		return base64Decoder;
	}

	@Required
	public void setBase64Decoder(Base64Decoder base64Decoder)
	{
		this.base64Decoder = base64Decoder;
	}

	public ByteArrayStreamDecoder getByteArrayStreamDecoder()
	{
		return byteArrayStreamDecoder;
	}

	@Required
	public void setByteArrayStreamDecoder(
			ByteArrayStreamDecoder byteArrayStreamDecoder)
	{
		this.byteArrayStreamDecoder = byteArrayStreamDecoder;
	}

	public AMF3ToJavaObjectDecoder getAmf3ToJavaObjectDecoder()
	{
		return amf3ToJavaObjectDecoder;
	}

	@Required
	public void setAmf3ToJavaObjectDecoder(
			AMF3ToJavaObjectDecoder amf3ToJavaObjectDecoder)
	{
		this.amf3ToJavaObjectDecoder = amf3ToJavaObjectDecoder;
	}

	public JavaObjectToAMF3Encoder getJavaObjectToAMF3Encoder()
	{
		return javaObjectToAMF3Encoder;
	}

	@Required
	public void setJavaObjectToAMF3Encoder(
			JavaObjectToAMF3Encoder javaObjectToAMF3Encoder)
	{
		this.javaObjectToAMF3Encoder = javaObjectToAMF3Encoder;
	}

	public Base64Encoder getBase64Encoder()
	{
		return base64Encoder;
	}

	@Required
	public void setBase64Encoder(Base64Encoder base64Encoder)
	{
		this.base64Encoder = base64Encoder;
	}

	public NulEncoder getNulEncoder()
	{
		return nulEncoder;
	}

	@Required
	public void setNulEncoder(NulEncoder nulEncoder)
	{
		this.nulEncoder = nulEncoder;
	}

}
