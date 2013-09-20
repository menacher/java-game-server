package io.nadron.handlers.netty;

import io.nadron.event.Events;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;

public class EventObjectDecoder extends LengthFieldBasedFrameDecoder
{

	/**
     * Creates a new instance.
     *
     * @param maxFrameLength
     *        the maximum length of the frame.  If the length of the frame is
     *        greater than this value, {@link TooLongFrameException} will be
     *        thrown.
     * @param lengthFieldOffset
     *        the offset of the length field
     * @param lengthFieldLength
     *        the length of the length field
     * @param lengthAdjustment
     *        the compensation value to add to the value of the length field
     * @param initialBytesToStrip
     *        the number of first bytes to strip out from the decoded frame
     */
	public EventObjectDecoder(int maxFrameLength, int lengthFieldOffset,
			int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment,
				initialBytesToStrip);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception 
	{
		
		ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        
        if (frame.readableBytes() > 0) 
		{
			byte opcode = frame.readByte();
			if (opcode == Events.NETWORK_MESSAGE) 
			{
				opcode = Events.SESSION_MESSAGE;
			}
			ByteBuf data = Unpooled.buffer(frame.readableBytes()).writeBytes(frame);
			Object obj = new SourceDecoder().decode(ctx, data);
			return Events.event(obj, opcode);
		}
		return null;
	}

	public static class SourceDecoder extends ObjectDecoder
	{
		public SourceDecoder() 
		{
			super(ClassResolvers.cacheDisabled(null));
		}
		
		@Override
		protected Object decode(ChannelHandlerContext ctx, ByteBuf in)
				throws Exception 
		{
			return super.decode(ctx, in);
		}
	}
}
