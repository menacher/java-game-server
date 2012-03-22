package org.menacheri.event.impl;

import org.menacheri.app.IPlayerSession;
import org.menacheri.app.ISession;
import org.menacheri.communication.IDeliveryGuaranty;
import org.menacheri.communication.IDeliveryGuaranty.DeliveryGuaranty;
import org.menacheri.communication.IMessageSender.IFast;
import org.menacheri.communication.IMessageSender.IReliable;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.event.INetworkEvent;
import org.menacheri.event.ISessionEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//TODO make it implement IEventlistener, session listener is not required the IEvent has context which can have the session attached.
public abstract class AbstractSessionEventHandler implements ISessionEventHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractSessionEventHandler.class);
	protected final int eventType;

	private ISession session = null;
	
	public AbstractSessionEventHandler()
	{
		this.eventType = Events.ANY;
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
	
	public void onDataIn(IEvent event)
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

	public void onNetworkMessage(INetworkEvent event)
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
	
	public void onLoginSuccess(IEvent event)
	{
		getSession().getTcpSender().sendMessage(event);
	}
	
	public void onLoginFailure(IEvent event)
	{
		getSession().getTcpSender().sendMessage(event);
	}
	
	public void onConnect(IEvent event)
	{
		Object source = event.getSource();
		if (source instanceof IReliable)
		{
			getSession().setTcpSender((IReliable) source);
		}
		else
		{
			if(null == getSession().getTcpSender())
			{
				logNullTcpConnection(event);
			}
			else
			{
				getSession().setUDPEnabled(true);
				getSession().setUdpSender((IFast) source);
			}
		}
	}

	public void onStart(IEvent event)
	{
		getSession().getTcpSender().sendMessage(event);
	}
	
	public void onStop(IEvent event)
	{
		getSession().getTcpSender().sendMessage(event);
	}
	
	public void onConnectFailed(IEvent event)
	{
		
	}

	public void onDisconnect(IEvent event)
	{
		LOG.debug("Received disconnect event in session. "
				+ "Going to close session");
		onClose(event);
	}
	
	public void onChangeAttribute(IEvent event)
	{

	}

	public void onException(IEvent event)
	{
		LOG.debug("Received exception event in session. "
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

	public ISession getSession()
	{
		return session;
	}

	public void setSession(ISession session)
	{
		this.session = session;
	}
	
	private void logNullTcpConnection(IEvent event){
		LOG.warn("Discarding {} as TCP connection is not fully "
				+ "established for this {}", event, getSession());
	}
}
