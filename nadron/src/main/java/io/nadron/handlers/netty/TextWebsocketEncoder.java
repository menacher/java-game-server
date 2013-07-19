package io.nadron.handlers.netty;

import io.nadron.event.Event;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * This encoder will convert an incoming object (mostly expected to be an
 * {@link Event} object) to a {@link TextWebSocketFrame} object. It uses
 * {@link ObjectMapper} from jackson library to do the Object to JSon String
 * encoding.
 * 
 * @author Abraham Menacherry
 * 
 */
@Sharable
public class TextWebsocketEncoder extends MessageToMessageEncoder<Event>
{

	private ObjectMapper jackson;

	@Override
	protected void encode(ChannelHandlerContext ctx, Event msg,
			List<Object> out) throws Exception
	{
		String json = jackson.writeValueAsString(msg);
		out.add(new TextWebSocketFrame(json));
	}

	public ObjectMapper getJackson()
	{
		return jackson;
	}

	public void setJackson(ObjectMapper jackson)
	{
		this.jackson = jackson;
	}

}
