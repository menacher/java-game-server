package org.menacheri.app.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.menacheri.app.ISession;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.event.IEventDispatcher;
import org.menacheri.event.IEventHandler;


/**
 * The default implementation of the session class. This class is responsible
 * for receiving and sending events. For receiving it uses the
 * {@link #onEvent(IEvent)} method and for sending it uses the
 * {@link IEventDispatcher} fireEvent method.
 * 
 * @author Abraham Menacherry
 * 
 */
public class Session implements ISession
{
	/**
	 * session id
	 */
	protected String id = null;
	/**
	 * event dispatcher
	 */
	protected IEventDispatcher eventDispatcher;

	/**
	 * session parameters
	 */
	private Map<String, Object> sessionAttributes;

	private long creationTime;

	private long lastReadWriteTime;

	private Status status;

	private boolean isWriteable;

	/**
	 * Life cycle variable to check if the session is shutting down. If it is, then no
	 * more incoming events will be accepted.
	 */
	volatile boolean isShuttingDown;
	
	private boolean isUDPEnabled;
	
	private Map<String, Object> connectParameters;

	public void initialize()
	{
		isShuttingDown = Boolean.valueOf(false);
		sessionAttributes = new HashMap<String, Object>();
		creationTime = System.currentTimeMillis();
		lastReadWriteTime = 0l;
		isWriteable = Boolean.valueOf(false);
		isUDPEnabled = Boolean.valueOf(false);
		connectParameters = new HashMap<String, Object>();
	}

	@Override
	public void onEvent(IEvent event)
	{
		if(!isShuttingDown){
			eventDispatcher.fireEvent(event);
		}
	}

	@Override
	public Object getId()
	{
		return id;
	}

	@Override
	public void setId(Object id)
	{
		this.id = id.toString();
	}

	@Override
	public IEventDispatcher getEventDispatcher()
	{
		return eventDispatcher;
	}

	@Override
	public void setEventDispatcher(IEventDispatcher eventDispatcher)
	{
		this.eventDispatcher = eventDispatcher;
	}

	@Override
	public void addHandler(IEventHandler eventHandler)
	{
		eventDispatcher.addHandler(eventHandler);
	}

	@Override
	public void removeHandler(IEventHandler eventHandler)
	{
		eventDispatcher.removeHandler(eventHandler);
	}
	
	@Override
	public List<IEventHandler> getEventHandlers(int eventType)
	{
		return eventDispatcher.getHandlers(eventType);
	}

	@Override
	public Object getAttribute(String key)
	{
		return sessionAttributes.get(key);
	}

	@Override
	public void removeAttribute(String key)
	{
		sessionAttributes.remove(key);
		IEvent event = Events.changeAttributeEvent(key, null);
		eventDispatcher.fireEvent(event);
	}

	@Override
	public void setAttribute(String key, Object value)
	{
		sessionAttributes.put(key, value);
		IEvent event = Events.changeAttributeEvent(key, value);
		eventDispatcher.fireEvent(event);
	}

	/**
	 * Get connect parameter
	 * 
	 * @param key
	 *            Find out a connect parameter for the this key.
	 * @return connect parameter
	 */
	public Object getConnectParameter(String key) {
		return this.connectParameters.get(key);
	}

	/**
	 * Set connect parameter
	 * 
	 * @param key
	 *            The key to be set. It should be a string.
	 * @param object
	 *            . The connection object to be set. If using a Netty
	 *            implementation, it would be {@link Channel}
	 */
	public void setConnectParameter(String key, Object object) {
		this.connectParameters.put(key, object);
	}

	/**
	 * Remove connect parameter
	 * 
	 * @param key
	 *            The connect parameter to be removed based on the key.
	 */
	public void removeConnectParameter(String key)
	{
		this.connectParameters.remove(key);
	}

	@Override
	public long getCreationTime()
	{
		return creationTime;
	}

	public void setCreationTime(long creationTime)
	{
		this.creationTime = creationTime;
	}

	@Override
	public long getLastReadWriteTime()
	{
		return lastReadWriteTime;
	}

	public void setLastReadWriteTime(long lastReadWriteTime)
	{
		this.lastReadWriteTime = lastReadWriteTime;
	}

	@Override
	public Status getStatus()
	{
		return status;
	}

	@Override
	public void setStatus(Status status)
	{
		this.status = status;

	}

	@Override
	public boolean isConnected()
	{
		return this.status == Status.CONNECTED;
	}

	@Override
	public boolean isWriteable()
	{
		return isWriteable;
	}

	@Override
	public void setWriteable(boolean isWriteable)
	{
		this.isWriteable = isWriteable;
	}

	/**
	 * Not synchronized because default implementation does not care whether a
	 * duplicated message sender is created.
	 * 
	 * @see org.menacheri.app.ISession#isUDPEnabled()
	 */
	public boolean isUDPEnabled()
	{
		return isUDPEnabled;
	}
	
	@Override
	public void setUDPEnabled(boolean isEnabled)
	{
		isUDPEnabled = isEnabled;
	}
	
	@Override
	public void close()
	{
		isShuttingDown = Boolean.valueOf(true);
		eventDispatcher.close();
		this.status = Status.CLOSED;
	}
	
	@Override
	public boolean isShuttingDown()
	{
		return isShuttingDown;
	}

	public Map<String, Object> getSessionAttributes()
	{
		return sessionAttributes;
	}

	public void setSessionAttributes(Map<String, Object> sessionAttributes)
	{
		this.sessionAttributes = sessionAttributes;
	}

	public Map<String, Object> getConnectParameters()
	{
		return connectParameters;
	}

	public void setConnectParameters(Map<String, Object> connectParameters)
	{
		this.connectParameters = connectParameters;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlayerSession other = (PlayerSession) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		return true;
	}
}
