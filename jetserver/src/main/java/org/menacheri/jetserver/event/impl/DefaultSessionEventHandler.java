package org.menacheri.jetserver.event.impl;

import org.menacheri.jetserver.app.PlayerSession;
import org.menacheri.jetserver.app.Session;
import org.menacheri.jetserver.communication.DeliveryGuaranty;
import org.menacheri.jetserver.communication.DeliveryGuaranty.DeliveryGuarantyOptions;
import org.menacheri.jetserver.communication.MessageSender.Fast;
import org.menacheri.jetserver.communication.MessageSender.Reliable;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.event.NetworkEvent;
import org.menacheri.jetserver.event.SessionEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class will handle any event that gets published to a
 * {@link Session#onEvent(Event)}. The event dispatcher will route all events
 * to this class's {@link #onEvent(Event)} method. It provides default
 * implementations for common events defined in the server. <b>Note</b> invoking
 * {@link #setSession(Session)} method on this class will result in an
 * {@link UnsupportedOperationException} since the session is a final variable
 * of this class.
 * 
 * @author Abraham Menacherry
 * 
 */
public class DefaultSessionEventHandler implements SessionEventHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSessionEventHandler.class);

	private final Session session;
	
	public DefaultSessionEventHandler(Session session)
	{
		this.session = session;
	}

	@Override
	public int getEventType()
	{
		return Events.ANY;
	}

	@Override
	public void onEvent(Event event)
	{
		doEventHandlerMethodLookup(event);
	}

	protected void doEventHandlerMethodLookup(Event event)
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
		case Events.CONNECT:
			onConnect(event);
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
	
	protected void onDataIn(Event event)
	{
		if (null != getSession())
		{
			PlayerSession pSession = (PlayerSession) getSession();
			NetworkEvent networkEvent = new DefaultNetworkEvent(event);
			if (pSession.isUDPEnabled())
			{
				networkEvent.setDeliveryGuaranty(DeliveryGuarantyOptions.FAST);
			}
			pSession.getGameRoom().sendBroadcast(networkEvent);
		}
	}

	protected void onNetworkMessage(NetworkEvent event)
	{
		DeliveryGuaranty guaranty = event.getDeliveryGuaranty();
		if(guaranty.getGuaranty() == DeliveryGuarantyOptions.FAST.getGuaranty()){
			Fast udpSender = getSession().getUdpSender();
			if(null != udpSender)
			{
				udpSender.sendMessage(event);
			}
			else
			{
				LOG.trace("Going to discard event: {} since udpSender is null in session: {}",event,session);
			}
		}else{
			getSession().getTcpSender().sendMessage(event);
		}
	}
	
	protected void onLoginSuccess(Event event)
	{
		getSession().getTcpSender().sendMessage(event);
	}
	
	protected void onLoginFailure(Event event)
	{
		getSession().getTcpSender().sendMessage(event);
	}
	
	protected void onConnect(Event event)
	{
		Object source = event.getSource();
		Session session = getSession();
		if (source instanceof Reliable)
		{
			session.setTcpSender((Reliable) source);
			// Now send the start event to session
			session.onEvent(Events.event(null, Events.START));
		}
		else
		{
			if(null == getSession().getTcpSender())
			{
				logNullTcpConnection(event);
			}
			else
			{
				session.setUDPEnabled(true);
				session.setUdpSender((Fast) source);
			}
		}
	}

	protected void onStart(Event event)
	{
		getSession().getTcpSender().sendMessage(event);
	}
	
	protected void onStop(Event event)
	{
		getSession().getTcpSender().sendMessage(event);
	}
	
	protected void onConnectFailed(Event event)
	{
		
	}

	protected void onDisconnect(Event event)
	{
		LOG.debug("Received disconnect event in session. "
				+ "Going to close session");
		onClose(event);
	}
	
	protected void onChangeAttribute(Event event)
	{

	}

	protected void onException(Event event)
	{
		LOG.debug("Received exception event in session. "
				+ "Going to close session");
		onClose(event);
	}

	protected void onClose(Event event)
	{
		getSession().close();
	}
	
	protected void onCustomEvent(Event event)
	{

	}

	public Session getSession()
	{
		return session;
	}

	public void setSession(Session session)
	{
		throw new UnsupportedOperationException("Session is a final variable and cannot be reset.");
	}
	
	private void logNullTcpConnection(Event event){
		LOG.warn("Discarding {} as TCP connection is not fully "
				+ "established for this {}", event, getSession());
	}
}
