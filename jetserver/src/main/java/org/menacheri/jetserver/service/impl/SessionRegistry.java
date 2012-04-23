package org.menacheri.jetserver.service.impl;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.menacheri.jetserver.app.Session;
import org.menacheri.jetserver.service.SessionRegistryService;


public class SessionRegistry implements SessionRegistryService
{
	private final Map<InetSocketAddress, Session> udpSessions;
	
	public SessionRegistry()
	{
		udpSessions = new ConcurrentHashMap<InetSocketAddress, Session>(1000);
	}
	
	@Override
	public Session getSession(Object key)
	{
		return udpSessions.get(key);
	}

	@Override
	public boolean putSession(Object key, Session session)
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
