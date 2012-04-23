package org.menacheri.jetserver.handlers.netty;

import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.menacheri.jetserver.convert.flex.AMFSerializer;
import org.menacheri.jetserver.convert.flex.SerializationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class will convert the incoming java object to Flex AMF3 byte format and
 * put them in a Netty {@link ChannelBuffer}. It will return this ChannelBuffer
 * to downstream handler.
 * 
 * @author Abraham Menacherry
 * 
 */
@Sharable
public class JavaObjectToAMF3Encoder extends OneToOneEncoder
{
	private static final Logger LOG = LoggerFactory.getLogger(JavaObjectToAMF3Encoder.class);
	
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		SerializationContextProvider contextProvider = new SerializationContextProvider();
		AMFSerializer serializer = new AMFSerializer(contextProvider.get());
		ByteArrayOutputStream baos = null;
		try 
		{
			baos = serializer.toAmf(msg);
		} 
		catch (IOException e) 
		{
			LOG.error("IO Error: {}",e);
			throw e;
		}
		return convertBAOSToChannelBuffer(baos);
	}

	/**
	 * Utility method to convert a byte array output stream object to a Netty
	 * channel buffer. This method will created a "wrapped" buffer which will
	 * not do any copy.
	 * 
	 * @param baos
	 *            The byte array output stream to convert.
	 * @return Returns the ChannelBuffer object or null if input is null.
	 */
	public ChannelBuffer convertBAOSToChannelBuffer(ByteArrayOutputStream baos)
	{
		if (null == baos)
			return null;
		
		return wrappedBuffer(baos.toByteArray());
	}
	
}
