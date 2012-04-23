package org.menacheri.jetserver.handlers.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is a very basic decoder which will decode the {@link ChannelBuffer} to
 * a byte array and send it onwards. It is useful for sending game operations as
 * bytes and should be used for fast paced games where object serialization
 * would be expensive. It can however be used with a serializer also, sine it is
 * sending a byte array.
 * 
 * @author Abraham Menacherry
 * 
 */
@Sharable
public class ByteArrayDecoder extends OneToOneDecoder
{
	private static final Logger LOG = LoggerFactory.getLogger(ByteArrayDecoder.class);
	
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception
	{
		if ((null == msg) || !(msg instanceof ChannelBuffer))
		{
			// TODO add an exception handler to the pipeline to deal with this
			// scenario.
			LOG.error("Incoming message not of ChannelBuffer type {}",msg);
			return msg;
		}

		ChannelBuffer buffer = (ChannelBuffer) msg;
		
		// TODO return this instead of byte array. This maybe more useful protocol.
		//buffer.toByteBuffer();
		return buffer.array();
	}
}
