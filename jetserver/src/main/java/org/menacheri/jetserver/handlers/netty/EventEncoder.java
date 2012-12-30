package org.menacheri.jetserver.handlers.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.menacheri.jetserver.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A simple event encoder will receive an incoming event, and convert it to a
 * {@link ChannelBuffer}. It will read the event type and put it as the
 * opcode(i.e first byte of the buffer), then it will read the event body and
 * put convert to ChannelBuffer if necessary and put it as the body of the
 * message.
 * 
 * @author Abraham Menacherry
 * 
 */
@Sharable
public class EventEncoder extends OneToOneEncoder
{
	private static final Logger LOG = LoggerFactory
			.getLogger(EventDecoder.class);

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception
	{
		if (null == msg)
		{
			LOG.error("Received null message in EventEncoder");
			return msg;
		}
		Event event = (Event) msg;
		ChannelBuffer opcode = ChannelBuffers.buffer(1);
		opcode.writeByte(event.getType());
		ChannelBuffer buffer = null;
		if(null != event.getSource())
		{
			ChannelBuffer data = (ChannelBuffer) event.getSource();
			buffer = ChannelBuffers.wrappedBuffer(opcode, data);
		}
		else
		{
			buffer = opcode;
		}
		return buffer;
	}

}
