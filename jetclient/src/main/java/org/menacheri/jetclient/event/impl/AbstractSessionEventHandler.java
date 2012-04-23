package org.menacheri.jetclient.event.impl;

import org.menacheri.jetclient.app.Session;
import org.menacheri.jetclient.communication.DeliveryGuaranty.DeliveryGuarantyOptions;
import org.menacheri.jetclient.communication.MessageSender;
import org.menacheri.jetclient.event.Events;
import org.menacheri.jetclient.event.Event;
import org.menacheri.jetclient.event.NetworkEvent;
import org.menacheri.jetclient.event.SessionEventHandler;

/**
 * Provides default implementation for most of the events. Subclasses can
 * override the event handler method for any particular event,
 * {@link AbstractSessionEventHandler}{@link #onDataIn(Event)} to do app
 * specific logic.
 * 
 * @author Abraham Menacherry
 * 
 */
public abstract class AbstractSessionEventHandler implements
		SessionEventHandler
{
	protected final int eventType;

	private final Session session;

	public AbstractSessionEventHandler(Session session)
	{
		this.eventType = Events.ANY;
		this.session = session;
	}

	@Override
	public int getEventType()
	{
		return eventType;
	}

	@Override
	public void onEvent(Event event)
	{
		doEventHandlerMethodLookup(event);
	}

	public void doEventHandlerMethodLookup(Event event)
	{
		int eventType = event.getType();
		switch (eventType)
		{
		case Events.SESSION_MESSAGE:
			onDataIn(event);
			break;
		case Events.NETWORK_MESSAGE:
			onNetworkMessage((NetworkEvent)event);
			break;
		case Events.LOG_IN_SUCCESS:
			onLoginSuccess(event);
			break;
		case Events.LOG_IN_FAILURE:
			onLoginFailure(event);
			break;
		case Events.START:
			onStart(event);
			break;
		case Events.STOP:
			onStart(event);
			break;
		case Events.CONNECT_FAILED:
			onConnectFailed(event);
			break;
		case Events.DISCONNECT:
			onDisconnect(event);
			break;
		case Events.CHANGE_ATTRIBUTE:
			onChangeAttribute(event);
			break;
		case Events.EXCEPTION:
			onException(event);
			break;
		default:
			onCustomEvent(event);
			break;
		}
	}

	public abstract void onDataIn(Event event);

	public void onNetworkMessage(NetworkEvent networkEvent)
	{
		Session session = getSession();
		boolean writeable = session.isWriteable();
		MessageSender messageSender = null;
		if (networkEvent.getDeliveryGuaranty().getGuaranty() == DeliveryGuarantyOptions.FAST
				.getGuaranty())
		{
			messageSender = session.getUdpMessageSender();
		}
		else
		{
			messageSender = session.getTcpMessageSender();
		}
		if (writeable && null != networkEvent)
		{
			messageSender.sendMessage(networkEvent);
		}
	}
	
	public void onLoginSuccess(Event event)
	{
	}

	public void onLoginFailure(Event event)
	{
	}

	public void onStart(Event event)
	{
		getSession().setWriteable(true);
	}

	public void onStop(Event event)
	{
		getSession().setWriteable(false);
	}

	public void onConnectFailed(Event event)
	{

	}

	public void onDisconnect(Event event)
	{
		System.out.println("Received disconnect event in session. "
				+ "Going to close session");
		onClose(event);
	}

	public void onChangeAttribute(Event event)
	{

	}

	public void onException(Event event)
	{
		System.out.println("Received exception event in session. "
				+ "Going to close session");
		onClose(event);
	}

	public void onClose(Event event)
	{
		getSession().close();
	}

	public void onCustomEvent(Event event)
	{

	}

	@Override
	public Session getSession()
	{
		return session;
	}

}
