package org.menacheri.jetclient.event.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.menacheri.jetclient.app.Session;
import org.menacheri.jetclient.event.Events;
import org.menacheri.jetclient.event.Event;
import org.menacheri.jetclient.event.EventDispatcher;
import org.menacheri.jetclient.event.EventHandler;
import org.menacheri.jetclient.event.SessionEventHandler;

/**
 * A synchronous dispatcher which will be used to dispatch incoming events on a
 * session to the appropriate {@link EventHandler}.
 * 
 * @author Abraham Menacherry
 * 
 */
public class DefaultEventDispatcher implements EventDispatcher
{
	private Map<Integer, List<EventHandler>> handlersByEventType;
	private List<EventHandler> genericHandlers;
	private boolean isShuttingDown;

	public DefaultEventDispatcher()
	{
		this(new HashMap<Integer, List<EventHandler>>(2),
				new CopyOnWriteArrayList<EventHandler>());
	}

	public DefaultEventDispatcher(
			Map<Integer, List<EventHandler>> handlersByEventType,
			List<EventHandler> genericHandlers)
	{
		this.handlersByEventType = handlersByEventType;
		this.genericHandlers = genericHandlers;
		this.isShuttingDown = false;
	}

	@Override
	public void addHandler(EventHandler eventHandler)
	{
		int eventType = eventHandler.getEventType();
		synchronized (this)
		{
			if (eventType == Events.ANY)
			{
				genericHandlers.add(eventHandler);
			}
			else
			{
				List<EventHandler> handlers = this.handlersByEventType
						.get(eventType);
				if (handlers == null)
				{
					handlers = new CopyOnWriteArrayList<EventHandler>();
					this.handlersByEventType.put(eventType, handlers);
				}

				handlers.add(eventHandler);
			}
		}
	}

	@Override
	public List<EventHandler> getHandlers(int eventType)
	{
		return handlersByEventType.get(eventType);
	}

	@Override
	public void removeHandler(EventHandler eventHandler)
	{
		int eventType = eventHandler.getEventType();
		synchronized (this)
		{
			if (eventType == Events.ANY)
			{
				genericHandlers.remove(eventHandler);
			}
			else
			{
				List<EventHandler> handlers = this.handlersByEventType
						.get(eventType);
				if (null != handlers)
				{
					handlers.remove(eventHandler);
					// Remove the reference if there are no listeners left.
					if (handlers.size() == 0)
					{
						handlersByEventType.put(eventType, null);
					}
				}
			}
		}

	}

	@Override
	public void removeHandlersForEvent(int eventType)
	{
		synchronized (this)
		{
			List<EventHandler> handlers = this.handlersByEventType
					.get(eventType);
			if (null != handlers)
			{
				handlers.clear();
			}
		}
	}

	@Override
	public boolean removeHandlersForSession(Session session)
	{
		List<EventHandler> removeList = new ArrayList<EventHandler>();
		Collection<List<EventHandler>> eventHandlersList = handlersByEventType
				.values();
		for (List<EventHandler> handlerList : eventHandlersList)
		{
			if (null != handlerList)
			{
				for (EventHandler handler : handlerList)
				{
					if (handler instanceof SessionEventHandler)
					{
						SessionEventHandler sessionHandler = (SessionEventHandler) handler;
						if (sessionHandler.getSession().equals(session))
						{
							removeList.add(handler);
						}
					}
				}
			}
		}
		for (EventHandler handler : removeList)
		{
			removeHandler(handler);
		}
		return (removeList.size() > 0);
	}

	@Override
	public synchronized void clear()
	{
		if(null != handlersByEventType)
		{
			handlersByEventType.clear();
		}
		if(null != genericHandlers)
		{
			genericHandlers.clear();
		}
	}
	
	@Override
	public void fireEvent(Event event)
	{
		boolean isShuttingDown = false;
		synchronized (this)
		{
			isShuttingDown = this.isShuttingDown;
		}
		if (!isShuttingDown)
		{
			for (EventHandler handler : genericHandlers)
			{
				handler.onEvent(event);
			}

			// retrieval is not thread safe, but since we are not setting it to
			// null
			// anywhere it should be fine.
			List<EventHandler> handlers = handlersByEventType.get(event
					.getType());
			// Iteration is thread safe since we use copy on write.
			if (null != handlers)
			{
				for (EventHandler handler : handlers)
				{
					handler.onEvent(event);
				}
			}
		}
		else
		{
			System.err.println("Discarding event: " + event
					+ " as dispatcher is shutting down");
		}

	}

	@Override
	public void close()
	{
		synchronized (this)
		{
			isShuttingDown = true;
			genericHandlers.clear();
			handlersByEventType.clear();
		}

	}

}
