package org.menacheri.jetserver.event;

import org.menacheri.jetserver.app.ISession;

public interface ISessionEventHandler extends IEventHandler
{
	public ISession getSession();

	public void setSession(ISession session) throws UnsupportedOperationException;
}
