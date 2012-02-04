package org.menacheri.service;

import org.menacheri.app.ISession;

public interface ISessionRegistryService
{
	public ISession getSession(Object key);
	
	public boolean putSession(Object key, ISession session);
	
	public boolean removeSession(Object key);
	// Add a session type object also to get udp/tcp/any
}
