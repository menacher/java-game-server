package io.nadron.event.impl;

import io.nadron.app.Session;
import io.nadron.event.Event;
import io.nadron.event.EventDispatcher;
import io.nadron.event.EventHandler;
import io.nadron.event.Events;
import io.nadron.event.SessionEventHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ExecutorEventDispatcher implements EventDispatcher
{
	private static final ExecutorService EXECUTOR = Executors
			.newSingleThreadExecutor();
	static
	{
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				EXECUTOR.shutdown();
			}
		});
	}

	private Map<Integer, List<EventHandler>> handlersByEventType;
	private List<EventHandler> genericHandlers;
	private boolean isShuttingDown;

	public ExecutorEventDispatcher()
	{
		this(new HashMap<Integer, List<EventHandler>>(2),
				new CopyOnWriteArrayList<EventHandler>());
	}

	public ExecutorEventDispatcher(
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
	public void fireEvent(final Event event)
	{
		boolean isShuttingDown = false;
		synchronized (this)
		{
			isShuttingDown = this.isShuttingDown;
		}
		if (!isShuttingDown)
		{
			EXECUTOR.submit(new Runnable()
			{

				@Override
				public void run()
				{
					for (EventHandler handler : genericHandlers)
					{
						handler.onEvent(event);
					}

					// retrieval is not thread safe, but since we are not
					// setting it to
					// null anywhere it should be fine.
					List<EventHandler> handlers = handlersByEventType
							.get(event.getType());
					// Iteration is thread safe since we use copy on write.
					if (null != handlers)
					{
						for (EventHandler handler : handlers)
						{
							handler.onEvent(event);
						}
					}

				}
			});

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
