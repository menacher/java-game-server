package org.menacheri.event.impl;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.menacheri.app.IPlayerSession;
import org.menacheri.app.ISession;
import org.menacheri.communication.IDeliveryGuaranty;
import org.menacheri.communication.IDeliveryGuaranty.DeliveryGuaranty;
import org.menacheri.communication.IMessageSender;
import org.menacheri.communication.IMessageSender.IFast;
import org.menacheri.communication.IMessageSender.IReliable;
import org.menacheri.communication.NettyUDPMessage;
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

	public AbstractSessionEventHandler(int eventType)
	{
		this.eventType = eventType;
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
		case Events.LOG_IN_UDP:
			onLoginUdp(event);
			break;
		case Events.LOG_IN_SUCCESS:
			onLoginSuccess(event);
			break;
		case Events.LOG_IN_FAILURE:
			onLoginFailure(event);
			break;
		case Events.CONNECT_TCP:
			onTcpConnect(event);
			break;
		case Events.CONNECT_UDP:
			onUdpConnect(event);
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
		if(null != getSession())
		{
			IPlayerSession pSession = (IPlayerSession)getSession();
			INetworkEvent networkEvent = new NetworkEvent(event);
			networkEvent.setDeliveryGuaranty(IDeliveryGuaranty.DeliveryGuaranty.FAST);
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
	
	public void onLoginUdp(IEvent event)
	{
		if (null != getSession().getTcpSender())
		{
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
	
	public void onTcpConnect(IEvent event)
	{
		IReliable tcpSender = (IReliable)createMessageSender(event.getSource());
		getSession().setTcpSender(tcpSender);
	}

	public void onUdpConnect(IEvent event)
	{
		IFast udpSender = (IFast)createMessageSender(event.getSource());
		if(null != udpSender)
		{
			getSession().setUdpSender((IFast)udpSender);
			getSession().setUDPEnabled(true);
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
		session.close();
	}
	
	public void onCustomEvent(IEvent event)
	{

	}

	/**
	 * Depending on the type of incoming connection, tcp, upd or even based on
	 * the type of network api used, Netty, MINA the connection would be
	 * different. This method will capture the incoming connection and create an
	 * appropriate {@link IMessageSender} for it.
	 * 
	 * @param nativeConnection
	 *            For Netty implementation it will be the {@link Channel} or a
	 *            {@link NettyUDPMessage} containing the {@link DatagramChannel}
	 *            and remote address of the udp client.
	 * @return Returns the created message sender for that particular network
	 *         type.
	 */
	public abstract IMessageSender createMessageSender(Object nativeConnection);
	
	public ISession getSession()
	{
		return session;
	}

	public void setSession(ISession session)
	{
		this.session = session;
	}
}
