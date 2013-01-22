package org.menacheri.jetserver.handlers.netty;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.impl.DefaultEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class TextWebsocketDecoder extends OneToOneDecoder
{

	private static final Logger LOG = LoggerFactory
			.getLogger(ByteArrayStreamDecoder.class);
	private Gson gson;

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception
	{
		Event event = null;
		try
		{
			TextWebSocketFrame frame = (TextWebSocketFrame) msg;
			event = gson.fromJson(frame.getText(), DefaultEvent.class);
			if (event.getType() == Events.NETWORK_MESSAGE)
			{
				event.setType(Events.SESSION_MESSAGE);
			}
		}
		catch (Exception e)
		{
			LOG.error("Exception occurred while decoding json: ", e);
		}
		return event;
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
