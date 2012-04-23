package org.menacheri.jetserver.event.impl;

import org.menacheri.jetserver.event.IEventDispatcher;

public class EventDispatchers
{
	public static IEventDispatcher newJetlangEventDispatcher()
	{
		JetlangEventDispatcher dispatcher = new JetlangEventDispatcher();
		dispatcher.initialize();
		return dispatcher;
	}
}
