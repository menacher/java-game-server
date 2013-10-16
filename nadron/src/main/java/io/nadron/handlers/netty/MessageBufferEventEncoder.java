package io.nadron.handlers.netty;

import io.nadron.communication.MessageBuffer;
import io.nadron.event.Event;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;



@Sharable
public class MessageBufferEventEncoder extends MessageToMessageEncoder<Event>
{

	@Override
	protected void encode(ChannelHandlerContext ctx, Event event,
			List<Object> out) throws Exception
	{
		out.add(encode(ctx, event));
	}
	
	/**
	 * Encode is separated out so that child classes can still reuse this
	 * functionality.
	 * 
	 * @param ctx
	 * @param event
	 *            The event to be encoded into {@link ByteBuf}. It will be
	 *            converted to 'opcode'-'payload' format.
	 * @return If only opcode is specified a single byte {@link ByteBuf} is
	 *         returned, otherwise a byte buf with 'opcode'-'payload' format is
	 *         returned.
	 */
	protected ByteBuf encode(ChannelHandlerContext ctx, Event event)
	{
		ByteBuf msg = null;
		if(null != event.getSource())
		{
			@SuppressWarnings("unchecked")
			MessageBuffer<ByteBuf> msgBuffer = (MessageBuffer<ByteBuf>)event.getSource();
			ByteBuf data = msgBuffer.getNativeBuffer();
			ByteBuf opcode = ctx.alloc().buffer(1);
			opcode.writeByte(event.getType());
			msg = Unpooled.wrappedBuffer(opcode, data);
		}
		else
		{
			msg = ctx.alloc().buffer(1);
			msg.writeByte(event.getType());
		}
		return msg;
	}

}
