package org.menacheri.jetserver.event.impl;

import org.menacheri.jetserver.communication.MessageSender.Reliable;
import org.menacheri.jetserver.event.Events;

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
