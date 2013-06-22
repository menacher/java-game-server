package io.nadron.app;

import io.nadron.communication.MessageSender;
import io.nadron.communication.MessageSender.Fast;
import io.nadron.communication.MessageSender.Reliable;
import io.nadron.event.Event;
import io.nadron.event.EventDispatcher;
import io.nadron.event.EventHandler;
import io.nadron.event.Events;

import java.util.List;



public interface Session
{
	/**
	 * session status types
	 */
	enum Status
	{
		NOT_CONNECTED, CONNECTING, CONNECTED, CLOSED
	}

	Object getId();

	void setId(Object id);

	void setAttribute(String key, Object value);

	Object getAttribute(String key);

	void removeAttribute(String key);

	void onEvent(Event event);

	EventDispatcher getEventDispatcher();


	boolean isWriteable();

	void setWriteable(boolean writeable);

	/**
	 * A session would not have UDP capability when created. Depending on the
	 * network abilities of the client, it can request UDP communication to be
	 * enabled with the LOGIN_UDP and CONNECT_UDP events of the {@link Events}
	 * class. Once UDP is enabled this flag will be set to true on the session.
	 * 
	 * @return Returns true if the a UDP {@link MessageSender} instance is
	 *         attached to this session, else false.
	 */
	boolean isUDPEnabled();
	
	/**
	 * A session would not have UDP capability when created. Depending on the
	 * network abilities of the client, it can request UDP communication to be
	 * enabled with the LOGIN_UDP and CONNECT_UDP events of the {@link Events}
	 * class. Once UDP {@link MessageSender} instance is attached to the
	 * session, this method should be called with flag to true to signal that
	 * the session is now UDP enabled.
	 * 
	 * @param isEnabled
	 *            Should be true in most use cases. It is used to signal that
	 *            the UDP {@link MessageSender} has been attached to session.
	 */
	void setUDPEnabled(boolean isEnabled);
	
	boolean isShuttingDown();
	
	long getCreationTime();

	long getLastReadWriteTime();

	void setStatus(Status status);

	Status getStatus();

	boolean isConnected();

	void addHandler(EventHandler eventHandler);
	
	void removeHandler(EventHandler eventHandler);
	
	List<EventHandler> getEventHandlers(int eventType);
	
	void close();

	public abstract void setUdpSender(Fast udpSender);

	public abstract Fast getUdpSender();

	public abstract void setTcpSender(Reliable tcpSender);

	public abstract Reliable getTcpSender();
}
