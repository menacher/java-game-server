package io.nadron.event.impl;

import io.nadron.communication.MessageSender.Reliable;
import io.nadron.event.Events;

public class ReconnectEvent extends DefaultConnectEvent
{
	private static final long serialVersionUID = 1L;

	public ReconnectEvent(Reliable tcpSender)
	{
		super(tcpSender, null);
	}

	public int getType()
	{
		return Events.RECONNECT;
	}
}
