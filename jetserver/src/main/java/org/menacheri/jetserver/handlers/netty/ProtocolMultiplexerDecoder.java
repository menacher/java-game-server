package org.menacheri.jetserver.handlers.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.menacheri.jetserver.util.BinaryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class can be used to switch login-protocol based on the incoming bytes
 * sent by a client. So, based on the incoming bytes, it is possible to set SSL
 * enabled, normal HTTP, default jetserver protocol, or custom user protocol for
 * allowing client to login to jetserver. The appropriate protocol searcher
 * needs to be injected via spring to this class.
 * 
 * @author Abraham Menacherry
 * 
 */
public class ProtocolMultiplexerDecoder extends FrameDecoder
{

	private static final Logger LOG = LoggerFactory
			.getLogger(ProtocolMultiplexerDecoder.class);

	private final LoginProtocol loginProtocol;
	private final int bytesForProtocolCheck;

	public ProtocolMultiplexerDecoder(int bytesForProtocolCheck,
			LoginProtocol loginProtocol)
	{
		this.loginProtocol = loginProtocol;
		this.bytesForProtocolCheck = bytesForProtocolCheck;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception
	{
		// Will use the first bytes to detect a protocol.
		if (buffer.readableBytes() < bytesForProtocolCheck)
		{
			return null;
		}

		ChannelPipeline pipeline = ctx.getPipeline();

		if (!loginProtocol.applyProtocol(buffer, pipeline))
		{
			byte[] headerBytes = new byte[bytesForProtocolCheck];
			buffer.getBytes(buffer.readerIndex(), headerBytes, 0,
					bytesForProtocolCheck);
			LOG.error(
					"Unknown protocol, discard everything and close the connection {}. Incoming Bytes {}",
					ctx.getChannel().getId(),
					BinaryUtils.getHexString(headerBytes));
			close(buffer, channel);
			return null;
		}
		else
		{
			pipeline.remove(this);
		}

		// Forward the current read buffer as is to the new handlers.
		return buffer.readBytes(buffer.readableBytes());
	}

	protected void close(ChannelBuffer buffer, Channel channel)
	{
		buffer.skipBytes(buffer.readableBytes());
		channel.close();
	}

	public LoginProtocol getLoginProtocol()
	{
		return loginProtocol;
	}

	public int getBytesForProtocolCheck()
	{
		return bytesForProtocolCheck;
	}

}
