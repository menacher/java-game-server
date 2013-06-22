package io.nadron.service;

import io.nadron.app.Session;

public interface SessionRegistryService<T>
{
	public Session getSession(T key);
	
	public boolean putSession(T key, Session session);
	
	public boolean removeSession(T key);
	// Add a session type object also to get udp/tcp/any
}
