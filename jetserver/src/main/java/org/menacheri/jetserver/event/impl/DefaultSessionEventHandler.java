package org.menacheri.jetserver.event.impl;

import org.menacheri.jetserver.app.PlayerSession;
import org.menacheri.jetserver.app.Session;
import org.menacheri.jetserver.communication.DeliveryGuaranty;
import static org.menacheri.jetserver.communication.DeliveryGuaranty.DeliveryGuarantyOptions.FAST;
import org.menacheri.jetserver.communication.MessageSender.Fast;
import org.menacheri.jetserver.event.ConnectEvent;
import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.NetworkEvent;
import org.menacheri.jetserver.event.SessionEventHandler;
import org.menacheri.jetserver.service.SessionRegistryService;
import org.menacheri.jetserver.util.JetConfig;
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
		switch (event.getType())
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
			onConnect((ConnectEvent)event);
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
			onChangeAttribute((ChangeAttributeEvent)event);
			break;
		case Events.EXCEPTION:
			onException(event);
			break;
		case Events.RECONNECT:
			onReconnect((ConnectEvent)event);
			break;
		case Events.LOG_OUT:
			onLogout(event);
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
				networkEvent.setDeliveryGuaranty(FAST);
			}
			pSession.getGameRoom().sendBroadcast(networkEvent);
		}
	}

	protected void onNetworkMessage(NetworkEvent event)
	{
		Session session = getSession();
		if (!session.isWriteable())
			return;
		DeliveryGuaranty guaranty = event.getDeliveryGuaranty();
		if (guaranty.getGuaranty() == FAST.getGuaranty())
		{
			Fast udpSender = session.getUdpSender();
			if (null != udpSender)
			{
				udpSender.sendMessage(event);
			}
			else
			{
				LOG.trace(
						"Going to discard event: {} since udpSender is null in session: {}",
						event, session);
			}
		}
		else
		{
			session.getTcpSender().sendMessage(event);
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
	
	protected void onConnect(ConnectEvent event)
	{
		Session session = getSession();
		if (null != event.getTcpSender())
		{
			session.setTcpSender(event.getTcpSender());
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
				session.setUdpSender(event.getUdpSender());
			}
		}
	}
	
	protected void onReconnect(ConnectEvent event)
	{
		Session session = getSession();
		// To synchronize with task for closing session in ReconnectRegistry service.
		synchronized(session){
			@SuppressWarnings("unchecked")
			SessionRegistryService<String> reconnectRegistry = ((SessionRegistryService<String>) session
					.getAttribute(JetConfig.RECONNECT_REGISTRY));
			if (null != reconnectRegistry && Session.Status.CLOSED != session.getStatus())
			{
				reconnectRegistry.removeSession((String) session
						.getAttribute(JetConfig.RECONNECT_KEY));
			}
		}
		onConnect(event);
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
		LOG.debug("Received disconnect event in session. ");
		onException(event);
	}
	
	protected void onChangeAttribute(ChangeAttributeEvent event)
	{
		getSession().setAttribute(event.getKey(), event.getValue());
	}

	@SuppressWarnings("unchecked")
	protected void onException(Event event)
	{
		Session session = getSession();
		session.setStatus(Session.Status.NOT_CONNECTED);
		session.setWriteable(false);
		session.setUDPEnabled(false);// will be set to true by udpupstream handler on connect event.
		String reconnectKey = (String) session
				.getAttribute(JetConfig.RECONNECT_KEY);
		SessionRegistryService<String> registry = (SessionRegistryService<String>)session.getAttribute(JetConfig.RECONNECT_REGISTRY);
		if (null != reconnectKey && null != registry)
		{
			// If session is already in registry then do not re-register.
			if(null == registry.getSession(reconnectKey)){
				registry.putSession(
						reconnectKey, getSession());
				LOG.debug("Received exception/disconnect event in session. "
					+ "Going to put session in reconnection registry");
			}
		}
		else
		{
			LOG.debug("Received exception/disconnect event in session. "
					+ "Going to close session");
			onClose(event);
		}
	}

	protected void onLogout(Event event)
	{
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
