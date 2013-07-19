package io.nadron.handlers.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * @author <a href="http://www.waywardmonkeys.com/">Bruce Mitchener</a>
 */
public class FlashPolicyServerDecoder extends ReplayingDecoder<ByteBuf> {
    // We don't check for the trailing NULL to make telnet-based debugging easier.
    private final ByteBuf requestBuffer = Unpooled.copiedBuffer("<policy-file-request/>", CharsetUtil.US_ASCII);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer,
			List<Object> out) throws Exception
	{
		ByteBuf data = buffer.readBytes(requestBuffer.readableBytes());
        if (data.equals(requestBuffer)) {
        	out.add(data);
        }
	}
}
