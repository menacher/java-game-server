package org.menacheri.jetserver.service;

import org.menacheri.jetserver.app.Session;

public interface SessionRegistryService
{
	public Session getSession(Object key);
	
	public boolean putSession(Object key, Session session);
	
	public boolean removeSession(Object key);
	// Add a session type object also to get udp/tcp/any
}
