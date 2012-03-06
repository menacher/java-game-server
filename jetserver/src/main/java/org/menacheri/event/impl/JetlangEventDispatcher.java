package org.menacheri.event.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.jetlang.channels.BatchSubscriber;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.core.Callback;
import org.jetlang.core.Disposable;
import org.jetlang.core.Filter;
import org.jetlang.fibers.Fiber;
import org.menacheri.app.ISession;
import org.menacheri.concurrent.Fibers;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.event.IEventDispatcher;
import org.menacheri.event.IEventHandler;
import org.menacheri.event.ISessionEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JetlangEventDispatcher implements IEventDispatcher
{
	private static final Logger LOG = LoggerFactory
			.getLogger(JetlangEventDispatcher.class);

	// TODO make it as a setter/constructor parameters
	private Map<Integer, List<IEventHandler>> handlersByEventType;
	private List<IEventHandler> anyHandler;
	private MemoryChannel<IEvent> eventQueue;
	private Fiber fiber;
	private volatile boolean isCloseCalled = false;
	/**
	 * This Map holds event handlers and their corresponding {@link Disposable}
	 * objects. This way, when a handler is removed, the dispose method can be
	 * called on the {@link Disposable}.
	 */
	private Map<IEventHandler, Disposable> disposableHandlerMap;

	public JetlangEventDispatcher()
	{

	}

	public JetlangEventDispatcher(
			Map<Integer, List<IEventHandler>> listenersByEventType,
			List<IEventHandler> anyHandler, MemoryChannel<IEvent> eventQueue,
			Fiber fiber)
	{
		super();
		this.handlersByEventType = listenersByEventType;
		this.anyHandler = anyHandler;
		this.eventQueue = eventQueue;
		this.fiber = fiber;
	}

	public void initialize()
	{
		// TODO make the 5 configurable.
		handlersByEventType = new HashMap<Integer, List<IEventHandler>>(4);
		anyHandler = new CopyOnWriteArrayList<IEventHandler>();
		eventQueue = new MemoryChannel<IEvent>();
		fiber = Fibers.pooledFiber();
		disposableHandlerMap = new HashMap<IEventHandler, Disposable>();
	}

	@Override
	public void fireEvent(final IEvent event)
	{
		eventQueue.publish(event);
	}

	@Override
	public void addHandler(final IEventHandler eventHandler)
	{
		final int eventType = eventHandler.getEventType();
		if (Events.ANY == eventType)
		{
			addANYHandler(eventHandler);
		}
		else
		{
			synchronized(this){
				List<IEventHandler> listeners = this.handlersByEventType.get(eventType);
				if (listeners == null)
				{
					listeners = new CopyOnWriteArrayList<IEventHandler>();
					this.handlersByEventType.put(eventType, listeners);
				}
		
				listeners.add(eventHandler);
		
				Callback<List<IEvent>> eventCallback = createEventCallbackForHandler(eventHandler);
		
				// Add the appropriate filter before processing the event.
				Filter<IEvent> eventFilter = new Filter<IEvent>()
				{
					@Override
					public boolean passes(IEvent msg)
					{
						return (eventHandler.getEventType() == msg.getType());
					}
				};
				// Create a subscription based on the filter also.
				BatchSubscriber<IEvent> batchEventSubscriber = new BatchSubscriber<IEvent>(
						fiber, eventCallback, eventFilter, 0, TimeUnit.MILLISECONDS);
				Disposable disposable = eventQueue.subscribe(batchEventSubscriber);
				disposableHandlerMap.put(eventHandler, disposable);
			}
		}
	}

	/**
	 * Creates a batch subscription to the jetlang memory channel for the ANY
	 * event handler. This method does not require synchronization since we are
	 * using CopyOnWriteArrayList
	 * 
	 * @param eventHandler
	 */
	protected void addANYHandler(final IEventHandler eventHandler)
	{
		final int eventType = eventHandler.getEventType();
		if (eventType != Events.ANY)
		{
			LOG.error("The incoming handler {} is not of type ANY",
					eventHandler);
			throw new IllegalArgumentException(
					"The incoming handler is not of type ANY");
		}
		Callback<List<IEvent>> eventCallback = createEventCallbackForHandler(eventHandler);
		BatchSubscriber<IEvent> batchEventSubscriber = new BatchSubscriber<IEvent>(
				fiber, eventCallback, 0, TimeUnit.MILLISECONDS);
		Disposable disposable = eventQueue.subscribe(batchEventSubscriber);
		disposableHandlerMap.put(eventHandler, disposable);
	}

	protected Callback<List<IEvent>> createEventCallbackForHandler(
			final IEventHandler eventHandler)
	{
		Callback<List<IEvent>> eventCallback = new Callback<List<IEvent>>()
		{
			@Override
			public void onMessage(List<IEvent> messages)
			{
				for (IEvent event : messages)
				{
					eventHandler.onEvent(event);
				}
			}
		};
		return eventCallback;
	}

	@Override
	public synchronized List<IEventHandler> getHandlers(int eventType)
	{
		if (Events.ANY == eventType)
		{
			return anyHandler;
		}
		return handlersByEventType.get(eventType);
	}

	@Override
	public void removeHandler(IEventHandler eventHandler)
	{
		int eventType = eventHandler.getEventType();
		if (Events.ANY == eventType)
		{
			anyHandler.remove(eventHandler);
		}
		else
		{
			synchronized(this){
				List<IEventHandler> listeners = this.handlersByEventType
						.get(eventType);
				if (null != listeners)
				{
					listeners.remove(eventHandler);
					// Remove the reference if there are no listeners left.
					if (listeners.size() == 0)
					{
						handlersByEventType.put(eventType, null);
					}
				}
			}
		}
		removeDisposableForHandler(eventHandler);
	}

	private synchronized void removeDisposableForHandler(IEventHandler eventHandler)
	{
		Disposable disposable = disposableHandlerMap.get(eventHandler);
		if (null != disposable)
		{
			disposable.dispose();
		}
	}

	@Override
	public synchronized void removeHandlersForEvent(int eventType)
	{
		List<IEventHandler> handlers = null;
		if (Events.ANY == eventType)
		{
			handlers = anyHandler;
		}
		else
		{
			handlers = this.handlersByEventType.get(eventType);
		}
		if (null != handlers)
		{
			for (IEventHandler eventHandler : handlers)
			{
				removeDisposableForHandler(eventHandler);
			}
			handlers.clear();
		}
		handlersByEventType.put(eventType, null);
	}

	public synchronized boolean removeHandlersForSession(ISession session)
	{
		LOG.trace("Entered removeHandlersForSession for session {}", session);
		List<IEventHandler> removeList = null;
		
		Collection<List<IEventHandler>> eventHandlersList = new ArrayList<List<IEventHandler>>(
				handlersByEventType.values());
		eventHandlersList.add(anyHandler);
		
		for (List<IEventHandler> handlerList : eventHandlersList)
		{
			removeList = getHandlersToRemoveForSession(handlerList,session);
		}
		
		LOG.trace("Going to remove {} handlers for session: {}",
				removeList.size(), session);
		for (IEventHandler handler : removeList)
		{
			removeHandler(handler);
		}
		return (removeList.size() > 0);
	}

	protected List<IEventHandler> getHandlersToRemoveForSession(
			List<IEventHandler> handlerList, ISession session)
	{
		List<IEventHandler> removeList = new ArrayList<IEventHandler>();
		if (null != handlerList)
		{
			for (IEventHandler handler : handlerList)
			{
				if (handler instanceof ISessionEventHandler)
				{
					ISessionEventHandler sessionHandler = (ISessionEventHandler) handler;
					if (sessionHandler.getSession().equals(session))
					{
						removeList.add(handler);
					}
				}
			}
		}
		return removeList;
	}
	
	@Override
	public synchronized void close()
	{
		if (!isCloseCalled)
		{
			fiber.dispose();
			eventQueue.clearSubscribers();
			// Iterate through the list of disposables and dispose each one.
			Collection<Disposable> disposables = disposableHandlerMap.values();
			for (Disposable disposable : disposables)
			{
				disposable.dispose();
			}
			handlersByEventType.clear();
			handlersByEventType = null;
			anyHandler.clear();
			anyHandler = null;
			isCloseCalled = true;
		}
	}

	public Map<Integer, List<IEventHandler>> getListenersByEventType()
	{
		return handlersByEventType;
	}

	public void setListenersByEventType(
			Map<Integer, List<IEventHandler>> listenersByEventType)
	{
		this.handlersByEventType = listenersByEventType;
	}

	public MemoryChannel<IEvent> getEventQueue()
	{
		return eventQueue;
	}

	public void setEventQueue(MemoryChannel<IEvent> eventQueue)
	{
		this.eventQueue = eventQueue;
	}

	public Fiber getFiber()
	{
		return fiber;
	}

	public void setFiber(Fiber fiber)
	{
		this.fiber = fiber;
	}

	public Map<IEventHandler, Disposable> getDisposableHandlerMap()
	{
		return disposableHandlerMap;
	}

	public void setDisposableHandlerMap(
			Map<IEventHandler, Disposable> disposableHandlerMap)
	{
		this.disposableHandlerMap = disposableHandlerMap;
	}

}
