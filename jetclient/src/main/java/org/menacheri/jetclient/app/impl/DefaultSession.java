package org.menacheri.jetclient.app.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.menacheri.jetclient.app.Session;
import org.menacheri.jetclient.communication.MessageSender.Fast;
import org.menacheri.jetclient.communication.MessageSender.Reliable;
import org.menacheri.jetclient.event.Events;
import org.menacheri.jetclient.event.Event;
import org.menacheri.jetclient.event.EventDispatcher;
import org.menacheri.jetclient.event.EventHandler;
import org.menacheri.jetclient.event.impl.DefaultEventDispatcher;

/**
 * The default implementation of the session class. This class is responsible
 * for receiving and sending events. For receiving it uses the
 * {@link #onEvent(Event)} method and for sending it uses the
 * {@link EventDispatcher} fireEvent method. Resetting id of this class after
 * creation will throw {@link IllegalAccessException} since class variable is
 * final.
 * 
 * @author Abraham Menacherry
 * 
 */
public class DefaultSession implements Session
{
	/**
	 * session id
	 */
	protected final Object id;
	/**
	 * event dispatcher
	 */
	protected final EventDispatcher eventDispatcher;

	/**
	 * session parameters
	 */
	protected final Map<String, Object> sessionAttributes;

	protected final long creationTime;

	protected long lastReadWriteTime;

	protected boolean isWriteable;

	/**
	 * Life cycle variable to check if the session is shutting down. If it is,
	 * then no more incoming events will be accepted.
	 */
	protected volatile boolean isShuttingDown;

	protected boolean isUDPEnabled;

	protected Reliable tcpMessageSender;
	protected Fast udpMessageSender;

	protected DefaultSession(SessionBuilder sessionBuilder)
	{
		// validate variables and provide default values if necessary. Normally
		// done in the builder.build() method, but done here since this class is
		// meant to be overriden and this could be easier.
		sessionBuilder.validateAndSetValues();
		this.id = sessionBuilder.id;
		this.eventDispatcher = sessionBuilder.eventDispatcher;
		this.sessionAttributes = sessionBuilder.sessionAttributes;
		this.creationTime = sessionBuilder.creationTime;
		this.lastReadWriteTime = sessionBuilder.lastReadWriteTime;
		this.isWriteable = sessionBuilder.isWriteable;
		this.isShuttingDown = sessionBuilder.isShuttingDown;
		this.isUDPEnabled = sessionBuilder.isUDPEnabled;
	}

	/**
	 * This class is roughly based on Joshua Bloch's Builder pattern. Since
	 * Session class will be extended by child classes, the
	 * {@link #validateAndSetValues()} method on this builder is actually called
	 * by the {@link DefaultSession} constructor for ease of use. May not be good
	 * design though.
	 * 
	 * @author Abraham, Menacherry
	 * 
	 */
	public static class SessionBuilder
	{
		private Object id = null;
		private EventDispatcher eventDispatcher = null;
		private Map<String, Object> sessionAttributes = null;
		private long creationTime = 0l;
		private long lastReadWriteTime = 0l;
		private boolean isWriteable = true;
		private volatile boolean isShuttingDown = false;
		private boolean isUDPEnabled = false;// By default UDP is not enabled.

		public Session build()
		{
			return new DefaultSession(this);
		}

		/**
		 * This method is used to validate and set the variables to default
		 * values if they are not already set before calling build. This method
		 * is invoked by the constructor of SessionBuilder. <b>Important!</b>
		 * Builder child classes which override this method need to call
		 * super.validateAndSetValues(), otherwise you could get runtime NPE's.
		 */
		protected void validateAndSetValues()
		{
			if (null == eventDispatcher)
			{
				eventDispatcher = new DefaultEventDispatcher();
			}
			if (null == sessionAttributes)
			{
				sessionAttributes = new HashMap<String, Object>();
			}
			creationTime = System.currentTimeMillis();
		}

		public Object getId()
		{
			return id;
		}

		public SessionBuilder id(final Object id)
		{
			this.id = id;
			return this;
		}

		public SessionBuilder eventDispatcher(
				final EventDispatcher eventDispatcher)
		{
			this.eventDispatcher = eventDispatcher;
			return this;
		}

		public SessionBuilder sessionAttributes(
				final Map<String, Object> sessionAttributes)
		{
			this.sessionAttributes = sessionAttributes;
			return this;
		}

		public SessionBuilder creationTime(long creationTime)
		{
			this.creationTime = creationTime;
			return this;
		}

		public SessionBuilder lastReadWriteTime(long lastReadWriteTime)
		{
			this.lastReadWriteTime = lastReadWriteTime;
			return this;
		}

		public SessionBuilder isWriteable(boolean isWriteable)
		{
			this.isWriteable = isWriteable;
			return this;
		}

		public SessionBuilder isShuttingDown(boolean isShuttingDown)
		{
			this.isShuttingDown = isShuttingDown;
			return this;
		}

		public SessionBuilder isUDPEnabled(boolean isUDPEnabled)
		{
			this.isUDPEnabled = isUDPEnabled;
			return this;
		}

	}

	@Override
	public void onEvent(Event event)
	{
		if (!isShuttingDown)
		{
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
		throw new RuntimeException(new IllegalAccessException(
				"id cannot be reset since it is a final variable. "
						+ "It is set at constuction time."));
	}

	@Override
	public EventDispatcher getEventDispatcher()
	{
		return eventDispatcher;
	}

	@Override
	public void addHandler(EventHandler eventHandler)
	{
		eventDispatcher.addHandler(eventHandler);
	}

	@Override
	public void removeHandler(EventHandler eventHandler)
	{
		eventDispatcher.removeHandler(eventHandler);
	}

	@Override
	public List<EventHandler> getEventHandlers(int eventType)
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
		Event event = Events.changeAttributeEvent(key, null);
		eventDispatcher.fireEvent(event);
	}

	@Override
	public void setAttribute(String key, Object value)
	{
		sessionAttributes.put(key, value);
		Event event = Events.changeAttributeEvent(key, value);
		eventDispatcher.fireEvent(event);
	}

	@Override
	public long getCreationTime()
	{
		return creationTime;
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
	 * @see Session#isUDPEnabled()
	 */
	@Override
	public boolean isUDPEnabled()
	{
		return isUDPEnabled;
	}

	@Override
	public void setUDPEnabled(boolean isEnabled)
	{
		this.isUDPEnabled = isEnabled;
	}

	@Override
	public void close()
	{
		isShuttingDown = true;
		eventDispatcher.close();
		if (null != tcpMessageSender)
		{
			tcpMessageSender.close();
			tcpMessageSender = null;
		}
		if (null != udpMessageSender)
		{
			udpMessageSender.close();
			udpMessageSender = null;
		}
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

	@Override
	public Reliable getTcpMessageSender()
	{
		return tcpMessageSender;
	}

	@Override
	public void setTcpMessageSender(Reliable tcpMessageSender)
	{
		this.tcpMessageSender = tcpMessageSender;
	}

	@Override
	public Fast getUdpMessageSender()
	{
		return udpMessageSender;
	}

	@Override
	public void setUdpMessageSender(Fast udpMessageSender)
	{
		this.udpMessageSender = udpMessageSender;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		DefaultSession other = (DefaultSession) obj;
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
