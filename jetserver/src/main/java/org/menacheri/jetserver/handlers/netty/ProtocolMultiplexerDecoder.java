package org.menacheri.jetserver.handlers.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundByteHandlerAdapter;
import io.netty.channel.ChannelPipeline;

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
public class ProtocolMultiplexerDecoder extends ChannelInboundByteHandlerAdapter
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
	protected void inboundBufferUpdated(ChannelHandlerContext ctx, 
			ByteBuf buffer) throws Exception
	{
		// Will use the first bytes to detect a protocol.
		if (buffer.readableBytes() < bytesForProtocolCheck)
		{
			return;
		}

		ChannelPipeline pipeline = ctx.pipeline();

		if (!loginProtocol.applyProtocol(buffer, pipeline))
		{
			byte[] headerBytes = new byte[bytesForProtocolCheck];
			buffer.getBytes(buffer.readerIndex(), headerBytes, 0,
					bytesForProtocolCheck);
			LOG.error(
					"Unknown protocol, discard everything and close the connection {}. Incoming Bytes {}",
					ctx.channel().id(),
					BinaryUtils.getHexString(headerBytes));
			close(buffer, ctx);
		}
		else
		{
			pipeline.removeAndForward(this);
		}

	}

	protected void close(ByteBuf buffer, ChannelHandlerContext ctx)
	{
		buffer.clear();
		ctx.close();
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
