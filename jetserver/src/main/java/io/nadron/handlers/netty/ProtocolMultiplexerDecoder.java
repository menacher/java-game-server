package io.nadron.handlers.netty;

import io.nadron.util.BinaryUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.MessageList;
import io.netty.handler.codec.ByteToMessageDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class can be used to switch login-protocol based on the incoming bytes
 * sent by a client. So, based on the incoming bytes, it is possible to set SSL
 * enabled, normal HTTP, default nadron protocol, or custom user protocol for
 * allowing client to login to nadron. The appropriate protocol searcher
 * needs to be injected via spring to this class.
 * 
 * @author Abraham Menacherry
 * 
 */
public class ProtocolMultiplexerDecoder extends ByteToMessageDecoder
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
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			MessageList<Object> out) throws Exception
	{
		// Will use the first bytes to detect a protocol.
		if (in.readableBytes() < bytesForProtocolCheck)
		{
			return;
		}

		ChannelPipeline pipeline = ctx.pipeline();

		if (!loginProtocol.applyProtocol(in, pipeline))
		{
			byte[] headerBytes = new byte[bytesForProtocolCheck];
			in.getBytes(in.readerIndex(), headerBytes, 0,
					bytesForProtocolCheck);
			LOG.error(
					"Unknown protocol, discard everything and close the connection {}. Incoming Bytes {}",
					ctx.channel().id(),
					BinaryUtils.getHexString(headerBytes));
			close(in, ctx);
		}
		else
		{
			pipeline.remove(this);
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
