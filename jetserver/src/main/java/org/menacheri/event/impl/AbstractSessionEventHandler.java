package org.menacheri.event.impl;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.menacheri.app.IPlayerSession;
import org.menacheri.app.ISession;
import org.menacheri.communication.IMessageSender;
import org.menacheri.communication.NettyUDPMessage;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.event.ISessionEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//TODO make it implement IEventlistener, session listener is not required the IEvent has context which can have the session attached.
public abstract class AbstractSessionEventHandler implements ISessionEventHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractSessionEventHandler.class);
	int eventType = Events.ANY;

	IMessageSender tcpSender = null;
	IMessageSender udpSender = null;
	
	private ISession session = null;
	
	public AbstractSessionEventHandler()
	{

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
		case Events.DATA_OUT_TCP:
			onTcpDataOut(event);
			break;
		case Events.DATA_OUT_UDP:
			onUdpDataOut(event);
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
		if(null != session)
		{
			IPlayerSession pSession = (IPlayerSession)session;
			pSession.getGameRoom().sendBroadcast(event.getSource());
		}
	}

	public void onTcpDataOut(IEvent event)
	{
		if (null != tcpSender)
		{
			tcpSender.sendMessage(event);
		}
	}

	public void onUdpDataOut(IEvent event)
	{
		if(null != udpSender)
		{
			udpSender.sendMessage(event);
		}
	}
	
	public void onLoginUdp(IEvent event)
	{
		if (null != tcpSender)
		{
			tcpSender.sendMessage(event);
		}
	}
	
	public void onLoginSuccess(IEvent event)
	{
		onTcpDataOut(event);
	}
	
	public void onLoginFailure(IEvent event)
	{
		onTcpDataOut(event);
	}
	
	public void onTcpConnect(IEvent event)
	{
		tcpSender = createMessageSender(event.getSource());
	}

	public void onUdpConnect(IEvent event)
	{
		udpSender = createMessageSender(event.getSource());
		if(null != udpSender)
		{
			getSession().setUDPEnabled(true);
		}
	}

	public void onStart(IEvent event)
	{
		onTcpDataOut(event);
	}
	
	public void onStop(IEvent event)
	{
		onTcpDataOut(event);
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
