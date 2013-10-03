package io.nadron.handlers.netty;

import io.nadron.event.Events;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;

import java.util.List;

public class EventObjectDecoder extends MessageToMessageDecoder<ByteBuf>
{

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if(null != in)
		{
			byte opcode = in.readByte();
			if (opcode == Events.NETWORK_MESSAGE) 
			{
				opcode = Events.SESSION_MESSAGE;
			}
			ByteBuf data = Unpooled.buffer(in.readableBytes()).writeBytes(in);
			// TODO check if creating a new object is necessary each time
			Object obj = new SourceDecoder().decode(ctx, data);
			out.add(Events.event(obj, opcode));
		}
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
