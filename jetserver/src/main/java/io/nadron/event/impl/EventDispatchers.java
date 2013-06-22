package io.nadron.event.impl;

import io.nadron.app.GameRoom;
import io.nadron.concurrent.Fibers;
import io.nadron.concurrent.Lane;
import io.nadron.concurrent.LaneStrategy;
import io.nadron.event.Event;
import io.nadron.event.EventDispatcher;

import java.util.concurrent.ExecutorService;

import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.Fiber;

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
