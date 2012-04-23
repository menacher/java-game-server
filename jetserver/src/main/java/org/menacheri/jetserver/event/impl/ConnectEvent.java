package org.menacheri.jetserver.event.impl;

import org.menacheri.jetserver.app.Session;
import org.menacheri.jetserver.communication.MessageSender;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.handlers.netty.LoginHandler;
import org.menacheri.jetserver.handlers.netty.UDPUpstreamHandler;

/**
 * This is a specific Event class with type {@link Events#CONNECT}. This class
 * is used by {@link LoginHandler} and {@link UDPUpstreamHandler} to create the
 * respective {@link MessageSender} (upd, or tcp), set it as the source of this
 * event and then forward it to the {@link Session}. <b>Note</b> Trying to
 * reset the event type of this class using {@link Event#setType(int)} will
 * result in an {@link UnsupportedOperationException}.
 * 
 * @author Abraham Menacherry
 * 
 */
public class ConnectEvent extends DefaultEvent
{
	private static final long serialVersionUID = 1L;
	private static final byte TYPE = Events.CONNECT;
	private MessageSender messageSender;

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
	public MessageSender getSource()
	{
		return messageSender;
	}

	@Override
	public void setSource(Object source)
	{
		this.messageSender = (MessageSender) source;
	}
}
