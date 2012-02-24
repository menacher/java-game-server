package org.menacheri.event.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jetlang.channels.ChannelSubscription;
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
	private static final Logger LOG = LoggerFactory.getLogger(JetlangEventDispatcher.class);
	
	// TODO make it as a setter/constructor parameters
	private Map<Integer, List<IEventHandler>> handlersByEventType;
	private MemoryChannel<IEvent> eventQueue;
	private Fiber fiber;
	private volatile boolean isCloseCalled = false;
	/**
	 * This Map holds event handlers and their corresponding {@link Disposable}
	 * objects. This way, when a handler is removed, the dispose method can be
	 * called on the {@link Disposable}.
	 */
	private Map<IEventHandler,Disposable> disposableHandlerMap;
	
	public JetlangEventDispatcher()
	{
		
	}
	
	public JetlangEventDispatcher(
			Map<Integer, List<IEventHandler>> listenersByEventType,
			MemoryChannel<IEvent> eventQueue, Fiber fiber)
	{
		super();
		this.handlersByEventType = listenersByEventType;
		this.eventQueue = eventQueue;
		this.fiber = fiber;
	}
	
	public void initialize()
	{
		//TODO make the 4 configurable.
		handlersByEventType = new HashMap<Integer, List<IEventHandler>>(4);
		eventQueue = new MemoryChannel<IEvent>();
		fiber = Fibers.pooledFiber();
		disposableHandlerMap = new HashMap<IEventHandler,Disposable>();
	}
	
	@Override
	public void fireEvent(final IEvent event)
	{
		eventQueue.publish(event);
	}

	@Override
	public synchronized void addHandler(final IEventHandler eventHandler)
	{
		final int eventType = eventHandler.getEventType();
		List<IEventHandler> listeners = this.handlersByEventType
				.get(eventType);
		if (listeners == null)
		{
			listeners = new CopyOnWriteArrayList<IEventHandler>();
			this.handlersByEventType.put(eventType, listeners);
		}

		listeners.add(eventHandler);

		Callback<IEvent> eventCallback = new Callback<IEvent>()
		{
			@Override
			public void onMessage(IEvent message)
			{
				eventHandler.onEvent(message);
			}
		};

		Disposable disposable = null;
		if (eventType == Events.ANY)
		{
			disposable = eventQueue.subscribe(fiber, eventCallback);
		}
		// It is a specific event listener
		else
		{
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
			ChannelSubscription<IEvent> subscription = new ChannelSubscription<IEvent>(
					fiber, eventCallback, eventFilter);
			disposable = eventQueue.subscribe(subscription);
			disposableHandlerMap.put(eventHandler, disposable);
		}
		
	}

	@Override
	public synchronized List<IEventHandler> getHandlers(int eventType)
	{
		return handlersByEventType.get(eventType);
	}

	@Override
	public synchronized void removeHandler(IEventHandler eventHandler)
	{
		int eventType = eventHandler.getEventType();
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
		Disposable disposable = disposableHandlerMap.get(eventHandler);
		if(null != disposable){
			disposable.dispose();
		}
	}

	@Override
	public synchronized void removeHandlersForEvent(int eventType)
	{
		List<IEventHandler> handlers = this.handlersByEventType
				.get(eventType);
		if (null != handlers)
		{
			for (IEventHandler eventHandler : handlers)
			{
				Disposable disposable = disposableHandlerMap.get(eventHandler);
				if(null != disposable){
					disposable.dispose();
				}
			}
		}
		handlersByEventType.put(eventType, null);
	}

	public synchronized boolean removeHandlersForSession(ISession session)
	{
		LOG.trace("Entered removeHandlersForSession for session {}",session);
		List<IEventHandler> removeList = new ArrayList<IEventHandler>();
		Collection<List<IEventHandler>> eventHandlersList = handlersByEventType.values();
		for(List<IEventHandler> handlerList:eventHandlersList)
		{
			if(null != handlerList)
			{
				for(IEventHandler handler:handlerList)
				{
					if(handler instanceof ISessionEventHandler)
					{
						ISessionEventHandler sessionHandler = (ISessionEventHandler)handler;
						if(sessionHandler.getSession().equals(session))
						{
							removeList.add(handler);
						}
					}
				}
			}
		}
		LOG.trace("Going to remove {} handlers for session: {}",removeList.size(),session);
		for(IEventHandler handler: removeList)
		{
			removeHandler(handler);
		}
		return (removeList.size() > 0);
	}
	
	@Override
	public synchronized void close()
	{
		if(!isCloseCalled)
		{
			fiber.dispose();
			eventQueue.clearSubscribers();
			// Iterate through the list of disposables and dispose each one.
			Collection<Disposable> disposables = disposableHandlerMap.values();
			for(Disposable disposable: disposables)
			{
				disposable.dispose();
			}
			handlersByEventType.clear();
			handlersByEventType = null;
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
