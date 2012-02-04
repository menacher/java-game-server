package org.menacheri.event.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jetlang.channels.MemoryChannel;
import org.jetlang.core.Callback;
import org.jetlang.core.Disposable;
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


public class EventDispatcher implements IEventDispatcher
{
	private static final Logger LOG = LoggerFactory.getLogger(EventDispatcher.class);
	private Map<Integer, List<IEventHandler>> listenersByEventType;
	private List<IEventHandler> genericListeners;
	/**
	 * This is a thread like construct, which will be used by the session to
	 * receive messages that are broadcast by the game room. This fiber will be
	 * used to subscribe to the game room channel and thus receive events
	 * asynchronously.
	 */
	private Fiber fiber;

	/**
	 * Whenever an event is retrieved by the fiber from the game room's
	 * {@link MemoryChannel}, this call back code is called to execute the event.
	 */
	private Callback<IEvent> eventCallback;
	
	private Disposable disposable;
	
	
	public EventDispatcher()
	{
		
	}
	
	public void initialize()
	{
		listenersByEventType = new HashMap<Integer, List<IEventHandler>>(2);
		genericListeners = new CopyOnWriteArrayList<IEventHandler>();
		initializeJetlang();
	}
	
	public void initializeJetlang()
	{
		fiber = Fibers.pooledFiber();
		// The call back will just pass the event to the fireEvent method of
		// this class.
		eventCallback = new Callback<IEvent>()
		{
			@Override
			public void onMessage(IEvent event)
			{
				fireEvent(event);
			}
		};
	}
	
	// TODO add a copyonwrite list for ANY types.

	@Override
	public void addHandler(IEventHandler eventListener)
	{
		int eventType = eventListener.getEventType();
		synchronized (this)
		{
			if(eventType == Events.ANY)
			{
				genericListeners.add(eventListener);
			}
			else
			{
				List<IEventHandler> listeners = this.listenersByEventType
						.get(eventType);
				if (listeners == null)
				{
					listeners = new CopyOnWriteArrayList<IEventHandler>();
					this.listenersByEventType.put(eventType, listeners);
				}
	
				listeners.add(eventListener);
			}
		}
	}

	@Override
	public List<IEventHandler> getHandlers(int eventType)
	{
		return listenersByEventType.get(eventType);
	}
	
	@Override
	public void fireEvent(IEvent event)
	{
		for(IEventHandler listener: genericListeners)
		{
			listener.onEvent(event);
		}
		
		// retrieval is not thread safe, but since we are not setting it to null
		// anywhere it should be fine.
		List<IEventHandler> listeners = listenersByEventType.get(event
				.getType());
		// Iteration is thread safe since we use copy on write.
		if (null != listeners)
		{
			for (IEventHandler listener : listeners)
			{
				listener.onEvent(event);
			}
		}
	}

	@Override
	public void removeHandler(IEventHandler eventListener)
	{
		int eventType = eventListener.getEventType();
		synchronized (this)
		{
			if(eventType == Events.ANY)
			{
				genericListeners.remove(eventListener);
			}
			else
			{
				List<IEventHandler> listeners = this.listenersByEventType
						.get(eventType);
				if (null != listeners)
				{
					listeners.remove(eventListener);
					// Remove the reference if there are no listeners left.
					if(listeners.size() == 0)
					{
						listenersByEventType.put(eventType, null);
					}
				}
			}
		}
	}

	@Override
	public synchronized void removeHandlersForEvent(int eventType)
	{
		List<IEventHandler> listeners = this.listenersByEventType
				.get(eventType);
		if (null != listeners)
		{
			listeners.clear();
		}
	}

	@Override
	public boolean removeHandlersForSession(ISession session)
	{
		LOG.trace("Entered removeHandlersForSession for session {}",session);
		List<IEventHandler> removeList = new ArrayList<IEventHandler>();
		Collection<List<IEventHandler>> eventHandlersList = listenersByEventType.values();
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
	public void close()
	{
		// TODO detach all listeners
		disposable.dispose();
		fiber.dispose();
	}
	
	@SuppressWarnings("unchecked")
	public Object subscribeToGameChannel(Object nativeGameChannel)
	{
		if ((nativeGameChannel != null))
		{
			if (nativeGameChannel instanceof MemoryChannel)
			{
				@SuppressWarnings("rawtypes")
				MemoryChannel memoryChannel = (MemoryChannel) nativeGameChannel;
				disposable = memoryChannel.subscribe(fiber, eventCallback);
			}
			else
			{
				LOG.error("subscribeToGameChannel nativeGameChannel not of type MemoryChannel");
			}
		}
		return disposable;
	}
}
