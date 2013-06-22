package io.nadron.service.impl;

import io.nadron.app.Session;
import io.nadron.service.SessionRegistryService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



public class SessionRegistry<T> implements SessionRegistryService<T>
{
	protected final Map<T, Session> sessions;
	
	public SessionRegistry()
	{
		sessions = new ConcurrentHashMap<T, Session>(1000);
	}
	
	@Override
	public Session getSession(T key)
	{
		return sessions.get(key);
	}

	@Override
	public boolean putSession(T key, Session session)
	{
		if(null == key ||  null == session)
		{
			return false;
		}
		
		if(null == sessions.put(key, session))
		{
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeSession(Object key)
	{
		return null != sessions.remove(key) ? true : false;
	}

}
