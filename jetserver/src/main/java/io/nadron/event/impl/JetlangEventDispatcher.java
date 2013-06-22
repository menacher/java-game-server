package io.nadron.event.impl;

import io.nadron.app.Session;
import io.nadron.concurrent.Lane;
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
import java.util.concurrent.TimeUnit;

import org.jetlang.channels.BatchSubscriber;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.core.Callback;
import org.jetlang.core.Disposable;
import org.jetlang.core.Filter;
import org.jetlang.fibers.Fiber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JetlangEventDispatcher implements EventDispatcher
{
	private static final Logger LOG = LoggerFactory
			.getLogger(JetlangEventDispatcher.class);

	// TODO make it as a setter/constructor parameters
	private Map<Integer, List<EventHandler>> handlersByEventType;
	private List<EventHandler> anyHandler;
	private final MemoryChannel<Event> eventQueue;
	private final Fiber fiber;
	private volatile boolean isCloseCalled = false;
	private final Lane<String, ExecutorService> dispatcherLane;
	
	/**
	 * This Map holds event handlers and their corresponding {@link Disposable}
	 * objects. This way, when a handler is removed, the dispose method can be
	 * called on the {@link Disposable}.
	 */
	private Map<EventHandler, Disposable> disposableHandlerMap;

	public JetlangEventDispatcher(MemoryChannel<Event> eventQueue, Fiber fiber, Lane<String, ExecutorService> lane)
	{
		this.eventQueue = eventQueue;
		this.fiber = fiber;
		this.dispatcherLane = lane;
	}

	public JetlangEventDispatcher(
			Map<Integer, List<EventHandler>> listenersByEventType,
			List<EventHandler> anyHandler, MemoryChannel<Event> eventQueue,
			Fiber fiber, Lane<String, ExecutorService> lane)
	{
		super();
		this.handlersByEventType = listenersByEventType;
		this.anyHandler = anyHandler;
		this.eventQueue = eventQueue;
		this.fiber = fiber;
		this.dispatcherLane = lane;
	}

	public void initialize()
	{
		// TODO make the 5 configurable.
		handlersByEventType = new HashMap<Integer, List<EventHandler>>(4);
		anyHandler = new CopyOnWriteArrayList<EventHandler>();
		disposableHandlerMap = new HashMap<EventHandler, Disposable>();
	}

	@Override
	public void fireEvent(final Event event)
	{
		if (null != dispatcherLane && dispatcherLane.isOnSameLane(Thread.currentThread().getName()))
		{
			dispatchEventOnSameLane(event);
		}
		else
		{
			eventQueue.publish(event);
		}
	}

	@Override
	public void addHandler(final EventHandler eventHandler)
	{
		final int eventType = eventHandler.getEventType();
		if (Events.ANY == eventType)
		{
			addANYHandler(eventHandler);
		}
		else
		{
			synchronized(this){
				List<EventHandler> listeners = this.handlersByEventType.get(eventType);
				if (listeners == null)
				{
					listeners = new CopyOnWriteArrayList<EventHandler>();
					this.handlersByEventType.put(eventType, listeners);
				}
		
				listeners.add(eventHandler);
		
				Callback<List<Event>> eventCallback = createEventCallbackForHandler(eventHandler);
		
				// Add the appropriate filter before processing the event.
				Filter<Event> eventFilter = new Filter<Event>()
				{
					@Override
					public boolean passes(Event msg)
					{
						return (eventHandler.getEventType() == msg.getType());
					}
				};
				// Create a subscription based on the filter also.
				BatchSubscriber<Event> batchEventSubscriber = new BatchSubscriber<Event>(
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
	protected void addANYHandler(final EventHandler eventHandler)
	{
		final int eventType = eventHandler.getEventType();
		if (eventType != Events.ANY)
		{
			LOG.error("The incoming handler {} is not of type ANY",
					eventHandler);
			throw new IllegalArgumentException(
					"The incoming handler is not of type ANY");
		}
		anyHandler.add(eventHandler);
		Callback<List<Event>> eventCallback = createEventCallbackForHandler(eventHandler);
		BatchSubscriber<Event> batchEventSubscriber = new BatchSubscriber<Event>(
				fiber, eventCallback, 0, TimeUnit.MILLISECONDS);
		Disposable disposable = eventQueue.subscribe(batchEventSubscriber);
		disposableHandlerMap.put(eventHandler, disposable);
	}

	protected Callback<List<Event>> createEventCallbackForHandler(
			final EventHandler eventHandler)
	{
		Callback<List<Event>> eventCallback = new Callback<List<Event>>()
		{
			@Override
			public void onMessage(List<Event> messages)
			{
				for (Event event : messages)
				{
					eventHandler.onEvent(event);
				}
			}
		};
		return eventCallback;
	}
	
	protected void dispatchEventOnSameLane(Event event)
	{
		for (EventHandler handler : anyHandler)
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

	@Override
	public synchronized List<EventHandler> getHandlers(int eventType)
	{
		if (Events.ANY == eventType)
		{
			return anyHandler;
		}
		return handlersByEventType.get(eventType);
	}

	@Override
	public void removeHandler(EventHandler eventHandler)
	{
		int eventType = eventHandler.getEventType();
		if (Events.ANY == eventType)
		{
			anyHandler.remove(eventHandler);
		}
		else
		{
			synchronized(this){
				List<EventHandler> listeners = this.handlersByEventType
						.get(eventType);
				if (null != listeners)
				{
					listeners.remove(eventHandler);
				}
			}
		}
		removeDisposableForHandler(eventHandler);
	}

	private synchronized void removeDisposableForHandler(EventHandler eventHandler)
	{
		Disposable disposable = disposableHandlerMap.get(eventHandler);
		if (null != disposable)
		{
			disposable.dispose();
			disposableHandlerMap.remove(eventHandler);
		}
	}

	@Override
	public synchronized void removeHandlersForEvent(int eventType)
	{
		List<EventHandler> handlers = null;
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
			for (EventHandler eventHandler : handlers)
			{
				removeDisposableForHandler(eventHandler);
			}
			handlers.clear();
		}
		handlersByEventType.put(eventType, null);
	}

	public synchronized boolean removeHandlersForSession(Session session)
	{
		LOG.trace("Entered removeHandlersForSession for session {}", session);
		List<EventHandler> removeList = new ArrayList<EventHandler>();
		
		Collection<List<EventHandler>> eventHandlersList = new ArrayList<List<EventHandler>>(
				handlersByEventType.values());
		eventHandlersList.add(anyHandler);
		
		for (List<EventHandler> handlerList : eventHandlersList)
		{
			removeList.addAll(getHandlersToRemoveForSession(handlerList,session));
		}
		
		LOG.trace("Going to remove {} handlers for session: {}",
				removeList.size(), session);
		for (EventHandler handler : removeList)
		{
			removeHandler(handler);
		}
		return (removeList.size() > 0);
	}

	@Override
	public synchronized void clear()
	{
		LOG.trace("Going to clear handlers on dispatcher {}", this);
		if(null != handlersByEventType)
		{
			handlersByEventType.clear();
		}
		if(null != anyHandler)
		{
			anyHandler.clear();
		}
		// Iterate through the list of disposables and dispose each one.
		Collection<Disposable> disposables = disposableHandlerMap.values();
		for (Disposable disposable : disposables)
		{
			disposable.dispose();
		}
		disposableHandlerMap.clear();
	}
	
	protected List<EventHandler> getHandlersToRemoveForSession(
			List<EventHandler> handlerList, Session session)
	{
		List<EventHandler> removeList = new ArrayList<EventHandler>();
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

	public Map<Integer, List<EventHandler>> getListenersByEventType()
	{
		return handlersByEventType;
	}

	public void setListenersByEventType(
			Map<Integer, List<EventHandler>> listenersByEventType)
	{
		this.handlersByEventType = listenersByEventType;
	}

	public MemoryChannel<Event> getEventQueue()
	{
		return eventQueue;
	}

	public Fiber getFiber()
	{
		return fiber;
	}

	public Map<EventHandler, Disposable> getDisposableHandlerMap()
	{
		return disposableHandlerMap;
	}

	public void setDisposableHandlerMap(
			Map<EventHandler, Disposable> disposableHandlerMap)
	{
		this.disposableHandlerMap = disposableHandlerMap;
	}

}
