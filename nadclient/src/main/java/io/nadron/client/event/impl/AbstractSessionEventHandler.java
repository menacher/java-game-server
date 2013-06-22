package io.nadron.client.event.impl;

import io.nadron.client.app.Session;
import io.nadron.client.communication.MessageBuffer;
import io.nadron.client.communication.MessageSender;
import io.nadron.client.communication.DeliveryGuaranty.DeliveryGuarantyOptions;
import io.nadron.client.event.Event;
import io.nadron.client.event.Events;
import io.nadron.client.event.NetworkEvent;
import io.nadron.client.event.SessionEventHandler;
import io.nadron.client.util.Config;
import io.netty.buffer.ByteBuf;


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
	protected static final int eventType = Events.ANY;

	protected Session session;
	
	protected volatile boolean isReconnecting = false;

	public AbstractSessionEventHandler()
	{
	}
	
	public AbstractSessionEventHandler(Session session)
	{
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
			onNetworkMessage((NetworkEvent) event);
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
		case Events.GAME_ROOM_JOIN_SUCCESS:
			onGameRoomJoin(event);
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

	public void onGameRoomJoin(Event event)
	{
		if (null != event.getSource()
				&& (event.getSource() instanceof MessageBuffer))
		{
			@SuppressWarnings("unchecked")
			String reconnectKey = ((MessageBuffer<ByteBuf>) event
					.getSource()).readString();
			if (null != reconnectKey)
				getSession().setAttribute(Config.RECONNECT_KEY, reconnectKey);
		}
	}

	public void onLoginFailure(Event event)
	{
	}

	public void onStart(Event event)
	{
		isReconnecting = false;
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
		//onException(event);
	}

	public void onChangeAttribute(Event event)
	{

	}

	public synchronized void onException(Event event)
	{
		Session session = getSession();
		String reconnectKey = (String) session
				.getAttribute(Config.RECONNECT_KEY);
		if (null != reconnectKey)
		{
			if(isReconnecting){
				return;
			}else{
				isReconnecting = true;
			}
			session.setWriteable(false);
			if (null != session.getReconnectPolicy())
			{
				session.getReconnectPolicy().applyPolicy(session);
			}
			else
			{
				System.err.println("Received exception event in session. "
						+ "Going to close session");
				onClose(event);
			}
		}
		else
		{
			System.err.println("Received exception event in session. "
					+ "Going to close session");
			onClose(event);
		}
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

	@Override
	public void setSession(Session session)
	{
		this.session = session;
	}
	
}
