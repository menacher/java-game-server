package io.nadron.event.impl;

import io.nadron.communication.MessageSender.Reliable;
import io.nadron.event.Events;

public class ReconnetEvent extends DefaultConnectEvent
{
	private static final long serialVersionUID = 1L;

	public ReconnetEvent(Reliable tcpSender)
	{
		super(tcpSender, null);
	}

	public int getType()
	{
		return Events.RECONNECT;
	}
}
