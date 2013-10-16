package io.nadron.handlers.netty;

import io.nadron.event.Event;
import io.nadron.event.Events;
import io.nadron.event.impl.DefaultEvent;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * This class will convert an incoming {@link TextWebSocketFrame} to an
 * {@link Event}. The incoming data is expected to be a JSon string
 * representation of an Event object. This class uses {@link ObjectMapper} to do
 * the decoding to {@link DefaultEvent}. If the incoming event is of type
 * {@link Events#NETWORK_MESSAGE} then it will be converted to
 * {@link Events#SESSION_MESSAGE}.
 * 
 * @author Abraham Menacherry
 * 
 */
@Sharable
public class TextWebsocketDecoder extends
		MessageToMessageDecoder<TextWebSocketFrame>
{

	private ObjectMapper jackson;

	/**
	 * This will be put into the {@link ChannelHandlerContext} the first time
	 * attr method is invoked on it. The get is also a set.
	 */
	private final AttributeKey<Class<? extends Event>> eventClass = new AttributeKey<Class<? extends Event>>(
			"eventClass");

	@Override
	protected void decode(ChannelHandlerContext ctx, TextWebSocketFrame frame,
			List<Object> out) throws Exception
	{
		// Get the existing class from the context. If not available, then
		// default to DefaultEvent.class
		Attribute<Class<? extends Event>> attr = ctx.attr(eventClass);
		Class<? extends Event> theClass = attr.get();
		boolean unknownClass = false;
		if (null == theClass)
		{
			unknownClass = true;
			theClass = DefaultEvent.class;
		}
		String json = frame.text();
		Event event = jackson.readValue(json, theClass);

		// If the class is unknown then either check if its the default event or
		// a different class. Put the right one in the context.
		if (unknownClass)
		{
			String cName = ((DefaultEvent) event).getcName();
			if (null == cName)
			{
				attr.set(DefaultEvent.class);
			}
			else
			{
				// Get the class from the string and de-serialize again. Since
				// thats the right class type.
				@SuppressWarnings("unchecked")
				Class<? extends Event> newClass = (Class<? extends Event>) Class
						.forName(cName);
				event = jackson.readValue(json, newClass);
				attr.set(newClass);
			}
		}

		if (event.getType() == Events.NETWORK_MESSAGE)
		{
			event.setType(Events.SESSION_MESSAGE);
		}
		out.add(event);
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
