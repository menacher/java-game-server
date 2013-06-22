package io.nadron.handlers.netty;

import io.nadron.convert.flex.AMFSerializer;
import io.nadron.convert.flex.SerializationContextProvider;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class will convert the incoming java object to Flex AMF3 byte format and
 * put them in a Netty {@link ByteBuf}. It will return this ChannelBuffer
 * to downstream handler.
 * 
 * @author Abraham Menacherry
 * 
 */
@Sharable
public class JavaObjectToAMF3Encoder extends MessageToByteEncoder<Object>
{
	private static final Logger LOG = LoggerFactory.getLogger(JavaObjectToAMF3Encoder.class);
	
	@Override
	protected void encode(ChannelHandlerContext ctx,
			Object msg, ByteBuf out) throws Exception {
		SerializationContextProvider contextProvider = new SerializationContextProvider();
		AMFSerializer serializer = new AMFSerializer(contextProvider.get());
		ByteArrayOutputStream baos = null;
		try 
		{
			baos = serializer.toAmf(msg);
			out.writeBytes(baos.toByteArray());
			baos.close();
		} 
		catch (IOException e) 
		{
			LOG.error("IO Error: {}",e);
			throw e;
		}
		
	}
	

}
