package io.nadron.handlers.netty;

import java.util.List;

import org.msgpack.MessagePack;

import io.nadron.event.Event;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToMessageEncoder;;

@Sharable
public class MsgPackEncoder extends MessageToMessageEncoder<Event> {

	MessagePack msgPack;
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Event event,
			List<Object> out) throws Exception
	{
		ByteBuf msg = null;
		if(null != event.getSource())
		{
			ByteBuf buf = ctx.alloc().buffer(1);
			buf.writeByte(event.getType());
			msg = Unpooled.wrappedBuffer(buf,
					Unpooled.wrappedBuffer(msgPack.write(event.getSource())));
		}
		else
		{
			msg = ctx.alloc().buffer(1);
			msg.writeByte(event.getType());
		}
		out.add(msg);
	}

	public MessagePack getMsgPack() {
		return msgPack;
	}

	public void setMsgPack(MessagePack msgPack) {
		this.msgPack = msgPack;
	}
	
}
