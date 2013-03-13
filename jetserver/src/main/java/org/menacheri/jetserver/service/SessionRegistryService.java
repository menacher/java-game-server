package org.menacheri.jetserver.service;

import org.menacheri.jetserver.app.Session;

public interface SessionRegistryService<T>
{
	public Session getSession(T key);
	
	public boolean putSession(T key, Session session);
	
	public boolean removeSession(T key);
	// Add a session type object also to get udp/tcp/any
}
