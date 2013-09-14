package io.nadron.client.app;

import io.nadron.client.communication.MessageSender;
import io.nadron.client.communication.ReconnectPolicy;
import io.nadron.client.communication.MessageSender.Fast;
import io.nadron.client.communication.MessageSender.Reliable;
import io.nadron.client.event.Event;
import io.nadron.client.event.EventDispatcher;
import io.nadron.client.event.EventHandler;
import io.nadron.client.event.Events;
import io.nadron.client.protocol.Protocol;
import io.nadron.client.util.LoginHelper;

import java.util.List;


/**
 * This interface abstracts a session in Nad client. A session can be thought of
 * as a high level connection to a remote nadron server. Internally it can have TCP
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
	
	/**
	 * Implementations will generally clear the internal netty pipeline and
	 * apply new set of handlers
	 * 
	 * @param protocol
	 */
	void resetProtocol(Protocol protocol);
	
	void reconnect(LoginHelper loginHelper);
	
	void setReconnectPolicy(ReconnectPolicy reconnectPolicy);
	
	ReconnectPolicy getReconnectPolicy();
}
