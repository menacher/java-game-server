package org.menacheri.event.impl;

import org.menacheri.app.ISession;
import org.menacheri.communication.IMessageSender;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.handlers.netty.LoginHandler;
import org.menacheri.handlers.netty.UDPUpstreamHandler;

/**
 * This is a specific Event class with type {@link Events#CONNECT}. This class
 * is used by {@link LoginHandler} and {@link UDPUpstreamHandler} to create the
 * respective {@link IMessageSender} (upd, or tcp), set it as the source of this
 * event and then forward it to the {@link ISession}. <b>Note</b> Trying to
 * reset the event type of this class using {@link IEvent#setType(int)} will
 * result in an {@link UnsupportedOperationException}.
 * 
 * @author Abraham Menacherry
 * 
 */
public class ConnectEvent extends Event
{
	private static final long serialVersionUID = 1L;
	private static final byte TYPE = Events.CONNECT;
	private IMessageSender messageSender;

	@Override
	public int getType()
	{
		return TYPE;
	}

	@Override
	public void setType(int type)
	{
		throw new UnsupportedOperationException(
				"Type field is final, it cannot be reset");
	}

	@Override
	public IMessageSender getSource()
	{
		return messageSender;
	}

	@Override
	public void setSource(Object source)
	{
		this.messageSender = (IMessageSender) source;
	}
}
