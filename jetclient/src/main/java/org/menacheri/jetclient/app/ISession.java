package org.menacheri.jetclient.app;

import java.util.List;

import org.menacheri.jetclient.communication.IMessageSender;
import org.menacheri.jetclient.communication.IMessageSender.IFast;
import org.menacheri.jetclient.communication.IMessageSender.IReliable;
import org.menacheri.jetclient.event.Events;
import org.menacheri.jetclient.event.IEvent;
import org.menacheri.jetclient.event.IEventDispatcher;
import org.menacheri.jetclient.event.IEventHandler;

/**
 * This interface abstracts a session in jetclient. A session can be thought of
 * as a high level connection to a remote jetserver. Internally it can have TCP
 * as well as UDP connections. The session also has event dispatching
 * capabilities. So when an event comes into the session, it will get dispatched
 * to the appropriate {@link IEventHandler}.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface ISession
{

	Object getId();

	void setId(Object id);

	void setAttribute(String key, Object value);

	Object getAttribute(String key);

	void removeAttribute(String key);

	void onEvent(IEvent event);

	IEventDispatcher getEventDispatcher();

	boolean isWriteable();

	void setWriteable(boolean writeable);

	/**
	 * A session would <b>not</b> have UDP capability when created. Depending on
	 * the network abilities of the client, it can request UDP communication to
	 * be enabled with the LOGIN_UDP and CONNECT_UDP events of the
	 * {@link Events} class. Once UDP is enabled this flag will be set to true
	 * on the session.
	 * 
	 * @return Returns true if the a UDP {@link IMessageSender} instance is
	 *         attached to this session, else false.
	 */
	boolean isUDPEnabled();

	/**
	 * A session would not have UDP capability when created. Depending on the
	 * network abilities of the client, it can request UDP communication to be
	 * enabled with the LOGIN_UDP and CONNECT_UDP events of the {@link Events}
	 * class. Once UDP {@link IMessageSender} instance is attached to the
	 * session, this method should be called with flag to true to signal that
	 * the session is now UDP enabled.
	 * 
	 * @param isEnabled
	 *            Should be true in most use cases. It is used to signal that
	 *            the UDP {@link IMessageSender} has been attached to session.
	 */
	void setUDPEnabled(boolean isEnabled);

	boolean isShuttingDown();

	long getCreationTime();

	long getLastReadWriteTime();

	/**
	 * This handler is actually added to the {@link IEventDispatcher}. This
	 * Method is provided as a helper for clients so that they need not deal
	 * with {@link IEventDispatcher} directly.
	 * 
	 * @param eventHandler
	 */
	void addHandler(IEventHandler eventHandler);

	void removeHandler(IEventHandler eventHandler);

	List<IEventHandler> getEventHandlers(int eventType);

	void close();

	void setUdpMessageSender(IFast udpMessageSender);

	IFast getUdpMessageSender();

	void setTcpMessageSender(IReliable tcpMessageSender);

	IReliable getTcpMessageSender();
}
