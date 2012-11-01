package org.menacheri.jetserver.protocols.impl;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.menacheri.jetserver.app.PlayerSession;
import org.menacheri.jetserver.handlers.netty.ByteArrayDecoder;
import org.menacheri.jetserver.handlers.netty.ByteArrayToChannelBufferEncoder;
import org.menacheri.jetserver.protocols.AbstractNettyProtocol;
import org.menacheri.jetserver.util.NettyUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * A protocol that can be used for fast paced games where operations and other
 * values can be sent as bytes which then get processed and converted to actual
 * java method operations. Messages with pay load like [OPCODE][PAYLOAD] will
 * find this protocol the most useful.
 * 
 * @author Abraham Menacherry
 * 
 */
public class SimpleByteArrayProtocol extends AbstractNettyProtocol
{
	/**
	 * Used to retrieve the rest of the bytes after the length field is
	 * stripped.
	 */
	private ByteArrayDecoder byteArrayDecoder;
	/**
	 * Converts a byte array to a {@link ChannelBuffer} while sending to the client.
	 */
	private ByteArrayToChannelBufferEncoder byteArrayToChannelBufferEncoder;
	/**
	 * Utility handler provided by netty to add the length of the outgoing
	 * message to the message as a header.
	 */
	private LengthFieldPrepender lengthFieldPrepender;

	public SimpleByteArrayProtocol()
	{
		super("SIMPLE_BYTE_ARRAY_PROTOCOL");
	}

	public SimpleByteArrayProtocol(ByteArrayDecoder byteArrayDecoder,
			LengthFieldPrepender lengthFieldPrepender)
	{
		super("SIMPLE_BYTE_ARRAY_PROTOCOL");
		this.byteArrayDecoder = byteArrayDecoder;
		this.lengthFieldPrepender = lengthFieldPrepender;
	}
	
	@Override
	public void applyProtocol(PlayerSession playerSession)
	{
		ChannelPipeline pipeline = NettyUtils
				.getPipeLineOfConnection(playerSession);
		// Upstream handlers or encoders (i.e towards server) are added to
		// pipeline now.
		pipeline.addLast("lengthDecoder", createLengthBasedFrameDecoder());
		pipeline.addLast("byteArrayDecoder", byteArrayDecoder);

		// Downstream handlers - Filter for data which flows from server to
		// client. Note that the last handler added is actually the first
		// handler for outgoing data.
		pipeline.addLast("byteArrayToChannelBufferEncoder", byteArrayToChannelBufferEncoder);
		pipeline.addLast("lengthFieldPrepender", lengthFieldPrepender);
	}

	public ByteArrayDecoder getByteArrayDecoder()
	{
		return byteArrayDecoder;
	}

	@Required
	public void setByteArrayDecoder(ByteArrayDecoder byteArrayDecoder)
	{
		this.byteArrayDecoder = byteArrayDecoder;
	}

	public LengthFieldPrepender getLengthFieldPrepender()
	{
		return lengthFieldPrepender;
	}

	@Required
	public void setLengthFieldPrepender(LengthFieldPrepender lengthFieldPrepender)
	{
		this.lengthFieldPrepender = lengthFieldPrepender;
	}

	public ByteArrayToChannelBufferEncoder getByteArrayToChannelBufferEncoder()
	{
		return byteArrayToChannelBufferEncoder;
	}

	@Required
	public void setByteArrayToChannelBufferEncoder(
			ByteArrayToChannelBufferEncoder byteArrayToChannelBufferEncoder)
	{
		this.byteArrayToChannelBufferEncoder = byteArrayToChannelBufferEncoder;
	}

}
