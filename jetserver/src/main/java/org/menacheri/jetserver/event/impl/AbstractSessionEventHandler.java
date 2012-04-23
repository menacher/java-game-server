package org.menacheri.jetserver.event.impl;

import org.menacheri.jetserver.app.IPlayerSession;
import org.menacheri.jetserver.app.ISession;
import org.menacheri.jetserver.communication.IDeliveryGuaranty;
import org.menacheri.jetserver.communication.IDeliveryGuaranty.DeliveryGuaranty;
import org.menacheri.jetserver.communication.IMessageSender.IFast;
import org.menacheri.jetserver.communication.IMessageSender.IReliable;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.IEvent;
import org.menacheri.jetserver.event.INetworkEvent;
import org.menacheri.jetserver.event.ISessionEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class will handle any event that gets published to a
 * {@link ISession#onEvent(IEvent)}. The event dispatcher will route all events
 * to this class's {@link #onEvent(IEvent)} method. It provides default
 * implementations for common events defined in the server. <b>Note</b> invoking
 * {@link #setSession(ISession)} method on this class will result in an
 * {@link UnsupportedOperationException} since the session is a final variable
 * of this class.
 * 
 * @author Abraham Menacherry
 * 
 */
public abstract class AbstractSessionEventHandler implements ISessionEventHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractSessionEventHandler.class);

	private final ISession session;
	
	public AbstractSessionEventHandler(ISession session)
	{
		this.session = session;
	}

	@Override
	public int getEventType()
	{
		return Events.ANY;
	}

	@Override
	public void onEvent(IEvent event)
	{
		doEventHandlerMethodLookup(event);
	}

	protected void doEventHandlerMethodLookup(IEvent event)
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
	
	protected void onDataIn(IEvent event)
	{
		if (null != getSession())
		{
			IPlayerSession pSession = (IPlayerSession) getSession();
			INetworkEvent networkEvent = new NetworkEvent(event);
			if (pSession.isUDPEnabled())
			{
				networkEvent.setDeliveryGuaranty(DeliveryGuaranty.FAST);
			}
			pSession.getGameRoom().sendBroadcast(networkEvent);
		}
	}

	protected void onNetworkMessage(INetworkEvent event)
	{
		IDeliveryGuaranty guaranty = event.getDeliveryGuaranty();
		if(guaranty.getGuaranty() == DeliveryGuaranty.FAST.getGuaranty()){
			IFast udpSender = getSession().getUdpSender();
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
	
	protected void onLoginSuccess(IEvent event)
	{
		getSession().getTcpSender().sendMessage(event);
	}
	
	protected void onLoginFailure(IEvent event)
	{
		getSession().getTcpSender().sendMessage(event);
	}
	
	protected void onConnect(IEvent event)
	{
		Object source = event.getSource();
		ISession session = getSession();
		if (source instanceof IReliable)
		{
			session.setTcpSender((IReliable) source);
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
				session.setUdpSender((IFast) source);
			}
		}
	}

	protected void onStart(IEvent event)
	{
		getSession().getTcpSender().sendMessage(event);
	}
	
	protected void onStop(IEvent event)
	{
		getSession().getTcpSender().sendMessage(event);
	}
	
	protected void onConnectFailed(IEvent event)
	{
		
	}

	protected void onDisconnect(IEvent event)
	{
		LOG.debug("Received disconnect event in session. "
				+ "Going to close session");
		onClose(event);
	}
	
	protected void onChangeAttribute(IEvent event)
	{

	}

	protected void onException(IEvent event)
	{
		LOG.debug("Received exception event in session. "
				+ "Going to close session");
		onClose(event);
	}

	protected void onClose(IEvent event)
	{
		getSession().close();
	}
	
	protected void onCustomEvent(IEvent event)
	{

	}

	public ISession getSession()
	{
		return session;
	}

	public void setSession(ISession session)
	{
		throw new UnsupportedOperationException("Session is a final variable and cannot be reset.");
	}
	
	private void logNullTcpConnection(IEvent event){
		LOG.warn("Discarding {} as TCP connection is not fully "
				+ "established for this {}", event, getSession());
	}
}
