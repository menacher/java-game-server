package org.menacheri.handlers.netty;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.menacheri.convert.ITransform;
import org.menacheri.convert.flex.AMFDeSerializer;
import org.menacheri.event.IEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import flex.messaging.io.SerializationContext;

/**
 * This class takes a {@link ChannelBuffer} containing AMF3 object as input and
 * creates a java object from it using the {@link AMFDeSerializer} class. If the
 * incoming event is of type {@link IEvent} then it will only de-serialize the
 * source of the event rather than the whole object.
 * 
 * @author Abraham Menacherry.
 * 
 */
@Sharable
public class AMF3ToJavaObjectDecoder extends OneToOneDecoder implements ITransform<ChannelBuffer, Object>
{
	private static final Logger LOG = LoggerFactory.getLogger(AMF3ToJavaObjectDecoder.class);
	
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception
	{
		if(null == msg)
		{
			LOG.warn("Incoming message is null");
			return msg;
		}
		ChannelBuffer buffer;
		if(msg instanceof IEvent){
			IEvent event = (IEvent)msg;
			buffer = (ChannelBuffer)event.getSource();
		}else{
			buffer = (ChannelBuffer)msg;
		}
		ByteArrayInputStream bis = new ByteArrayInputStream(buffer.array());
		return deSerializeObjectFromStream(bis);
	}

	@Override
	public Object convert(ChannelBuffer buffer) throws Exception {
		ByteArrayInputStream bis = new ByteArrayInputStream(buffer.array());
		return deSerializeObjectFromStream(bis);
	}
	
	protected Object deSerializeObjectFromStream(ByteArrayInputStream bis) throws Exception
	{
		AMFDeSerializer serializer = new AMFDeSerializer(SerializationContext
				.getSerializationContext());
		Object o = null;
		try
		{
			// do the deserialization.
			o = serializer.fromAmf(bis);
			LOG.trace("Serialized object: {}",o);
		}
		catch (IOException e)
		{
			LOG.error("IO error in AMF3ToJavaObjectDecoder: {}",e);
			throw e;
		}
		catch (ClassNotFoundException e)
		{
			LOG.error("Error in AMF3ToJavaObjectDecoder: {}.\n " +
					"Check if flash class has corresponding java class",e);
			throw e;
		}
		return o;
	}
}
