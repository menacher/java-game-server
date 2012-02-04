package org.menacheri.event;

import org.menacheri.app.ISession;
import org.menacheri.event.impl.AbstractSessionEventHandler;
import org.menacheri.event.impl.ChangeAttributeEvent;
import org.menacheri.event.impl.Event;
import org.menacheri.event.impl.EventContext;


public class Events
{
	/**
	 * Events should never have this type. But event handlers can choose to have
	 * this type to signify that they will handle any type of incoming event.
	 * For e.g. {@link AbstractSessionEventHandler}
	 */
	public final static byte ANY = 0x00;
	// Lifecycle events.
	public final static byte CONNECT_TCP = 0x02;
	public final static byte CONNECT_UDP = 0x04;
	public final static byte CONNECT_FAILED = 0x06;
	public static final byte LOG_IN = 0x08;
	public static final byte LOG_IN_UDP = 0x09;
	public static final byte LOG_OUT = 0x0a;
	public static final byte LOG_IN_SUCCESS = 0x0b;
	public static final byte LOG_IN_FAILURE = 0x0c;
	public static final byte LOG_IN_FAILURE_UDP = 0x0d;
	public static final byte LOG_OUT_SUCCESS = 0x0e;
	public static final byte LOG_OUT_FAILURE = 0x0f;
	
	// Metadata events
	public static final byte GAME_LIST = 0x10;
	public static final byte ROOM_LIST = 0x12;
	public static final byte GAME_ROOM_JOIN = 0x14;
	public static final byte GAME_ROOM_LEAVE = 0x16;
	public static final byte GAME_ROOM_JOIN_SUCCESS = 0x18;
	public static final byte GAME_ROOM_JOIN_FAILURE = 0x19;
	
	/**
	 * Event sent from server to client to start message sending from client to server.
	 */
	public static final byte START = 0x1a;
	
	/**
	 * Event sent from server to client to stop messages from being sent to server.
	 */
	public static final byte STOP = 0x1b;
	/**
	 * Incoming data from another machine/JVM to this JVM (server or client)
	 */
	public final static byte SESSION_MESSAGE = 0x1c;

	/**
	 * Outgoing data from this JVM to another remote machine/JVM using TCP as
	 * the socket transport protocol
	 */
	public final static byte DATA_OUT_TCP = 0x1d;
	
	/**
	 * Outgoing data from this JVM to another remote machine/JVM using UDP as
	 * the socket transport protocol
	 */
	public final static byte DATA_OUT_UDP = 0x1e;
	
	public final static byte CHANGE_ATTRIBUTE = 0x20;
	
	/**
	 * If a remote connection is forcibly disconnected then raise this event.
	 */
	public final static byte DISCONNECT = 0x22;
	public final static byte EXCEPTION = 0x24;
	
	public static IEvent event(Object source, int eventType)
	{
		return event(source,eventType,(ISession)null);
	}
	
	public static IEvent event(Object source, int eventType,ISession session)
	{
		IEventContext context = null;
		if(null != session)
		{
			context = new EventContext();
		}
		return event(source,eventType,context);
	}
	
	public static IEvent event(Object source, int eventType, IEventContext context)
	{
		Event event = new Event();
		event.setSource(source);
		event.setType(eventType);
		event.setEventContext(context);
		event.setTimeStamp(System.currentTimeMillis());
		return event;
	}
	
	public static IEvent dataOutTcpEvent(Object source)
	{
		return event(source,Events.DATA_OUT_TCP);
	}
	
	public static IEvent dataOutUdpEvent(Object source)
	{
		return event(source,Events.DATA_OUT_UDP);
	}
	
	public static IEvent dataInEvent(Object source)
	{
		return event(source,Events.SESSION_MESSAGE);
	}
	
	public static IEvent changeAttributeEvent(String key, Object value)
	{
		ChangeAttributeEvent changeAttributeEvent = new ChangeAttributeEvent();
		changeAttributeEvent.setType(CHANGE_ATTRIBUTE);
		changeAttributeEvent.setKey(key);
		changeAttributeEvent.setValue(value);
		return changeAttributeEvent;
	}
}
