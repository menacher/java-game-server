package org.menacheri.jetserver.handlers.netty;

import io.netty.buffer.MessageBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.impl.DefaultEvent;

import com.google.gson.Gson;

/**
 * This class will convert an incoming {@link TextWebSocketFrame} to an
 * {@link Event}. The incoming data is expected to be a JSon string
 * representation of an Event object. This class uses {@link Gson} to do the
 * decoding to {@link DefaultEvent}. If the incoming event is of type
 * {@link Events#NETWORK_MESSAGE} then it will be converted to
 * {@link Events#SESSION_MESSAGE}.
 * 
 * @author Abraham Menacherry
 * 
 */
@Sharable
public class TextWebsocketDecoder extends MessageToMessageDecoder<TextWebSocketFrame>
{

	private Gson gson;

	@Override
	protected void decode(ChannelHandlerContext ctx, TextWebSocketFrame frame,
			MessageBuf<Object> out) throws Exception
	{
		Event event = gson.fromJson(frame.text(), DefaultEvent.class);
		if (event.getType() == Events.NETWORK_MESSAGE)
		{
			event.setType(Events.SESSION_MESSAGE);
		}
		out.add(event);
	}
	
	public Gson getGson()
	{
		return gson;
	}

	public void setGson(Gson gson)
	{
		this.gson = gson;
	}

}
