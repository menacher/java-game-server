package io.nadron.client.event;

import io.nadron.client.communication.DeliveryGuaranty;
import io.nadron.client.communication.DeliveryGuaranty.DeliveryGuarantyOptions;
import io.nadron.client.event.impl.AbstractSessionEventHandler;
import io.nadron.client.event.impl.ChangeAttributeEvent;
import io.nadron.client.event.impl.DefaultEvent;
import io.nadron.client.event.impl.DefaultNetworkEvent;
/**
 * Defines the event constants. Server and client communicate to each other
 * using these constants. However, since Netty can be used to support any binary
 * protocol, this need not be the only set of constants and users can write
 * their own protocols.
 * 
 * @author Abraham Menacherry
 * 
 */
public class Events
{
	/**
	 * The protocol version. This will be sent along with the login message so
	 * that appropriate login handlers can be set in the pipeline.
	 */
	public static final byte PROTOCOL_VERSION = 0X01;
	/**
	 * Events should <b>NEVER</b> have this type. But event handlers can choose
	 * to have this type to signify that they will handle any type of incoming
	 * event. For e.g. {@link AbstractSessionEventHandler}
	 */
	public final static byte ANY = 0x00;
	// Lifecycle events.
	public final static byte CONNECT = 0x02;
	/**
	 * Similar to LOG_IN but parameters are different. This event is sent from
	 * client to server.
	 */
	public static final byte RECONNECT = 0x3;
	public final static byte CONNECT_FAILED = 0x06;
	public static final byte LOG_IN = 0x08;
	public static final byte LOG_OUT = 0x0a;
	public static final byte LOG_IN_SUCCESS = 0x0b;
	public static final byte LOG_IN_FAILURE = 0x0c;
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
	 * Event sent from server to client to start message sending from client to
	 * server.
	 */
	public static final byte START = 0x1a;

	/**
	 * Event sent from server to client to stop messages from being sent to
	 * server.
	 */
	public static final byte STOP = 0x1b;
	/**
	 * Incoming data from another machine/JVM to this JVM (server or client)
	 */
	public final static byte SESSION_MESSAGE = 0x1c;

	/**
	 * This event is used to send data from the current machine to remote
	 * machines using TCP or UDP transports. It is an out-going event.
	 */
	public static final byte NETWORK_MESSAGE = 0x1d;
	
	public final static byte CHANGE_ATTRIBUTE = 0x20;

	/**
	 * If a remote connection is disconnected or closed then raise this event.
	 */
	public final static byte DISCONNECT = 0x22;
	public final static byte EXCEPTION = 0x24;

	/**
	 * Creates a network event with the source set to the object passed in as
	 * parameter and the {@link DeliveryGuaranty} set to
	 * {@link DeliveryGuarantyOptions#RELIABLE}. This method delegates to
	 * {@link #networkEvent(Object, DeliveryGuaranty)}.
	 * 
	 * @param source
	 *            The payload of the event. This is the actual data that gets
	 *            transmitted to remote machine.
	 * @return An instance of {@link NetworkEvent}
	 */
	public static NetworkEvent networkEvent(Object source)
	{
		return networkEvent(source, DeliveryGuarantyOptions.RELIABLE);
	}
	
	/**
	 * Creates a network event with the source set to the object passed in as
	 * parameter and the {@link DeliveryGuaranty} set to the incoming
	 * parameter.
	 * 
	 * @param source
	 *            The payload of the event. This is the actual data that gets
	 *            transmitted to remote machine.
	 * @param deliveryGuaranty
	 *            This decides which transport TCP or UDP to be used to send the
	 *            message to remote machine.
	 * @return An instance of {@link NetworkEvent}
	 */
	public static NetworkEvent networkEvent(Object source, DeliveryGuaranty deliveryGuaranty)
	{
		Event event = event(source,Events.NETWORK_MESSAGE);
		NetworkEvent networkEvent = new DefaultNetworkEvent(event);
		networkEvent.setDeliveryGuaranty(deliveryGuaranty);
		return networkEvent;
	}
	
	public static Event event(Object source, int eventType)
	{
		DefaultEvent event = new DefaultEvent();
		event.setSource(source);
		event.setType(eventType);
		event.setTimeStamp(System.currentTimeMillis());
		return event;
	}

	public static Event changeAttributeEvent(String key, Object value)
	{
		ChangeAttributeEvent changeAttributeEvent = new ChangeAttributeEvent();
		changeAttributeEvent.setType(CHANGE_ATTRIBUTE);
		changeAttributeEvent.setKey(key);
		changeAttributeEvent.setValue(value);
		return changeAttributeEvent;
	}
}
