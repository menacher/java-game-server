package org.menacheri.jetserver.service.impl;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.menacheri.jetserver.app.ISession;
import org.menacheri.jetserver.service.ISessionRegistryService;


public class SessionRegistry implements ISessionRegistryService
{
	private final Map<InetSocketAddress, ISession> udpSessions;
	
	public SessionRegistry()
	{
		udpSessions = new ConcurrentHashMap<InetSocketAddress, ISession>(1000);
	}
	
	@Override
	public ISession getSession(Object key)
	{
		return udpSessions.get(key);
	}

	@Override
	public boolean putSession(Object key, ISession session)
	{
		if(null == key ||  null == session)
		{
			return false;
		}
		
		if(null == udpSessions.put((InetSocketAddress)key, session))
		{
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeSession(Object key)
	{
		if(null != udpSessions.remove(key))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

}
