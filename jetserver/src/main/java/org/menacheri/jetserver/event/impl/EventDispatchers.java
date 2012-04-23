package org.menacheri.jetserver.event.impl;

import org.menacheri.jetserver.event.EventDispatcher;

public class EventDispatchers
{
	public static EventDispatcher newJetlangEventDispatcher()
	{
		JetlangEventDispatcher dispatcher = new JetlangEventDispatcher();
		dispatcher.initialize();
		return dispatcher;
	}
}
