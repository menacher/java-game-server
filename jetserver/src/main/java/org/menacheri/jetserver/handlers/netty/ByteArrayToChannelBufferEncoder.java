package org.menacheri.jetserver.handlers.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts a byte array object to a {@link ChannelBuffer}. It actually wraps
 * the byte array with the {@link ChannelBuffer} instance using wrappedBuffer
 * method.
 * 
 * @author Abraham Menacherry
 * 
 */
@Sharable
public class ByteArrayToChannelBufferEncoder extends OneToOneEncoder
{
	private static final Logger LOG = LoggerFactory.getLogger(AMF3ToJavaObjectDecoder.class);
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception
	{
		if (!(msg instanceof byte[]))
		{
			LOG.error("Invalid object type passed into encoder: {} "
					+ "Expected byte[] object in class "
					+ "ByteArrayToChannelBufferEncoder.",msg.getClass().getName());
			return msg;
		}
		byte[] byteArray = (byte[]) msg;
		LOG.trace("Converting byte array to channel buffer in ByteArrayToChannelBufferEncoder");
		ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(byteArray);
		return buffer;
	}

}
