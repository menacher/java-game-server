package org.menacheri.event.impl;

import org.jetlang.channels.MemoryChannel;
import org.jetlang.core.Callback;
import org.jetlang.core.Disposable;
import org.jetlang.fibers.Fiber;
import org.menacheri.concurrent.Fibers;
import org.menacheri.event.IEvent;


public class AsyncSessionEventHandler extends NettySessionEventHandler
{
	private final MemoryChannel<IEvent> eventQueue = new MemoryChannel<IEvent>();
	private final Fiber fiber = Fibers.pooledFiber();
	private final Callback<IEvent> onEvent = new Callback<IEvent>()
	{
		public void onMessage(IEvent message) {
			// TODO add exception handling in case jetlang does not allow next
			// message to be published after uncaught exception.
			doEventHandlerMethodLookup(message);
		};
	};
	private final Disposable disposable = eventQueue.subscribe(fiber,onEvent);
	
	@Override
	public void onEvent(IEvent event)
	{
		if(null == event) return;
		
		eventQueue.publish(event);
	}
	
	@Override
	public void onDisconnect(IEvent event)
	{
		// required for closing the message sender
		super.onDisconnect(event);
		fiber.dispose();
		disposable.dispose();
	}
	
	
}
