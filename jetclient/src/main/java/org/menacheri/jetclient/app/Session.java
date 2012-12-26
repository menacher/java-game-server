package org.menacheri.jetclient.app;

import java.util.List;

import org.menacheri.jetclient.communication.MessageSender;
import org.menacheri.jetclient.communication.MessageSender.Fast;
import org.menacheri.jetclient.communication.MessageSender.Reliable;
import org.menacheri.jetclient.event.Events;
import org.menacheri.jetclient.event.Event;
import org.menacheri.jetclient.event.EventDispatcher;
import org.menacheri.jetclient.event.EventHandler;

/**
 * This interface abstracts a session in jetclient. A session can be thought of
 * as a high level connection to a remote jetserver. Internally it can have TCP
 * as well as UDP connections. The session also has event dispatching
 * capabilities. So when an event comes into the session, it will get dispatched
 * to the appropriate {@link EventHandler}.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface Session
{

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
	 * A session would <b>not</b> have UDP capability when created. Depending on
	 * the network abilities of the client, it can request UDP communication to
	 * be enabled with the LOGIN_UDP and CONNECT_UDP events of the
	 * {@link Events} class. Once UDP is enabled this flag will be set to true
	 * on the session.
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

	/**
	 * This handler is actually added to the {@link EventDispatcher}. This
	 * Method is provided as a helper for clients so that they need not deal
	 * with {@link EventDispatcher} directly.
	 * 
	 * @param eventHandler
	 */
	void addHandler(EventHandler eventHandler);

	void removeHandler(EventHandler eventHandler);

	List<EventHandler> getEventHandlers(int eventType);

	void close();

	void setUdpMessageSender(Fast udpMessageSender);

	Fast getUdpMessageSender();

	void setTcpMessageSender(Reliable tcpMessageSender);

	Reliable getTcpMessageSender();
}
