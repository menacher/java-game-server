package org.menacheri.jetserver.handlers.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import org.menacheri.jetserver.event.Event;

import com.google.gson.Gson;

/**
 * This encoder will convert an incoming object (mostly expected to be an
 * {@link Event} object) to a {@link TextWebSocketFrame} object. It uses
 * {@link Gson} to do the Object to JSon String encoding.
 * 
 * @author Abraham Menacherry
 * 
 */
@Sharable
public class TextWebsocketEncoder extends MessageToMessageEncoder<Event>
{

	private Gson gson;

	@Override
	protected Object encode(ChannelHandlerContext ctx, 
			Event msg) throws Exception
	{
		String json = gson.toJson(msg);
		return new TextWebSocketFrame(json);
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
