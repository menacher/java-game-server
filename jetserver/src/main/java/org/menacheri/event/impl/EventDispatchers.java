package org.menacheri.event.impl;

import org.menacheri.event.IEventDispatcher;

public class EventDispatchers
{
	public static IEventDispatcher newEventDispatcher()
	{
		EventDispatcher dispatcher = new EventDispatcher();
		dispatcher.initialize();
		return dispatcher;
	}
	
	public static IEventDispatcher newJetlangEventDispatcher()
	{
		JetlangEventDispatcher dispatcher = new JetlangEventDispatcher();
		dispatcher.initialize();
		return dispatcher;
	}
}
