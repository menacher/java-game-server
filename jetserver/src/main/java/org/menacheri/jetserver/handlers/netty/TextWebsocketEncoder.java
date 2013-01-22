package org.menacheri.jetserver.handlers.netty;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
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
public class TextWebsocketEncoder extends OneToOneEncoder
{

	private Gson gson;

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception
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
