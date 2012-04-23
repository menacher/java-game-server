package org.menacheri.jetserver.app;

import java.util.List;

import org.menacheri.jetserver.communication.IMessageSender;
import org.menacheri.jetserver.communication.IMessageSender.IFast;
import org.menacheri.jetserver.communication.IMessageSender.IReliable;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.IEvent;
import org.menacheri.jetserver.event.IEventDispatcher;
import org.menacheri.jetserver.event.IEventHandler;


public interface ISession
{
	/**
	 * session status types
	 */
	enum Status
	{
		NOT_CONNECTED, CONNECTING, CONNECTED,CLOSED
	}

	Object getId();

	void setId(Object id);

	void setAttribute(String key, Object value);

	Object getAttribute(String key);

	void removeAttribute(String key);

	/**
     * Set connect parameter
     *
     * @param key
     * @param object
     */
    public void setConnectParameter( String key, Object object );

    /**
     * Remove connect parameter
     *
     * @param key
     */
    public void removeConnectParameter( String key );
    
    /**
     * Get connect parameter
     *
     * @param key
     * @return connect parameter
     */
    public Object getConnectParameter( String key );
    
	void onEvent(IEvent event);

	IEventDispatcher getEventDispatcher();


	boolean isWriteable();

	void setWriteable(boolean writeable);

	/**
	 * A session would not have UDP capability when created. Depending on the
	 * network abilities of the client, it can request UDP communication to be
	 * enabled with the LOGIN_UDP and CONNECT_UDP events of the {@link Events}
	 * class. Once UDP is enabled this flag will be set to true on the session.
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

	void setStatus(Status status);

	Status getStatus();

	boolean isConnected();

	void addHandler(IEventHandler eventHandler);
	
	void removeHandler(IEventHandler eventHandler);
	
	List<IEventHandler> getEventHandlers(int eventType);
	
	void close();

	public abstract void setUdpSender(IFast udpSender);

	public abstract IFast getUdpSender();

	public abstract void setTcpSender(IReliable tcpSender);

	public abstract IReliable getTcpSender();
}
