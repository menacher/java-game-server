package org.menacheri.event;

import org.menacheri.app.ISession;

public interface ISessionEventHandler extends IEventHandler
{
	public ISession getSession();

	public void setSession(ISession session);
}
