package org.menacheri.jetserver.event.impl;

import java.util.concurrent.ExecutorService;

import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.Fiber;
import org.menacheri.jetserver.app.GameRoom;
import org.menacheri.jetserver.concurrent.Fibers;
import org.menacheri.jetserver.concurrent.Lane;
import org.menacheri.jetserver.concurrent.LaneStrategy;
import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.event.EventDispatcher;

public class EventDispatchers
{
	public static EventDispatcher newJetlangEventDispatcher(GameRoom room,
			LaneStrategy<String, ExecutorService, GameRoom> strategy)
	{
		Fiber fiber = null;
		JetlangEventDispatcher dispatcher = null;
		if (null == room)
		{
			fiber = Fibers.pooledFiber();
			dispatcher = new JetlangEventDispatcher(new MemoryChannel<Event>(),
					fiber, null);
		}
		else
		{
			Lane<String, ExecutorService> lane = strategy.chooseLane(room);
			fiber = Fibers.pooledFiber(lane);
			dispatcher = new JetlangEventDispatcher(new MemoryChannel<Event>(),
					fiber, lane);
		}
		dispatcher.initialize();

		return dispatcher;
	}
}
