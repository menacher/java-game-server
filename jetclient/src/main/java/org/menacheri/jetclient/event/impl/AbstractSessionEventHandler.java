package org.menacheri.jetclient.event.impl;

import org.menacheri.jetclient.app.ISession;
import org.menacheri.jetclient.communication.IDeliveryGuaranty.DeliveryGuaranty;
import org.menacheri.jetclient.communication.IMessageSender;
import org.menacheri.jetclient.event.Events;
import org.menacheri.jetclient.event.IEvent;
import org.menacheri.jetclient.event.INetworkEvent;
import org.menacheri.jetclient.event.ISessionEventHandler;

/**
 * Provides default implementation for most of the events. Subclasses can
 * override the event handler method for any particular event,
 * {@link AbstractSessionEventHandler}{@link #onDataIn(IEvent)} to do app
 * specific logic.
 * 
 * @author Abraham Menacherry
 * 
 */
public abstract class AbstractSessionEventHandler implements
		ISessionEventHandler
{
	protected final int eventType;

	private final ISession session;

	public AbstractSessionEventHandler(ISession session)
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
	public void onEvent(IEvent event)
	{
		doEventHandlerMethodLookup(event);
	}

	public void doEventHandlerMethodLookup(IEvent event)
	{
		int eventType = event.getType();
		switch (eventType)
		{
		case Events.SESSION_MESSAGE:
			onDataIn(event);
			break;
		case Events.NETWORK_MESSAGE:
			onNetworkMessage((INetworkEvent)event);
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

	public abstract void onDataIn(IEvent event);

	public void onNetworkMessage(INetworkEvent networkEvent)
	{
		ISession session = getSession();
		boolean writeable = session.isWriteable();
		IMessageSender messageSender = null;
		if (networkEvent.getDeliveryGuaranty().getGuaranty() == DeliveryGuaranty.FAST
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
	
	public void onLoginSuccess(IEvent event)
	{
	}

	public void onLoginFailure(IEvent event)
	{
	}

	public void onStart(IEvent event)
	{
		getSession().setWriteable(true);
	}

	public void onStop(IEvent event)
	{
		getSession().setWriteable(false);
	}

	public void onConnectFailed(IEvent event)
	{

	}

	public void onDisconnect(IEvent event)
	{
		System.out.println("Received disconnect event in session. "
				+ "Going to close session");
		onClose(event);
	}

	public void onChangeAttribute(IEvent event)
	{

	}

	public void onException(IEvent event)
	{
		System.out.println("Received exception event in session. "
				+ "Going to close session");
		onClose(event);
	}

	public void onClose(IEvent event)
	{
		getSession().close();
	}

	public void onCustomEvent(IEvent event)
	{

	}

	@Override
	public ISession getSession()
	{
		return session;
	}

}
