package org.menacheri.jetserver.event.impl;

import org.menacheri.jetserver.app.ISession;
import org.menacheri.jetserver.event.IEventContext;


public class EventContext implements IEventContext
{

	private Object attachement;
	private ISession session;

	@Override
	public Object getAttachment()
	{
		return attachement;
	}

	@Override
	public ISession getSession()
	{
		return session;
	}

	@Override
	public void setAttachment(Object attachement)
	{
		this.attachement = attachement;
	}

	@Override
	public void setSession(ISession session)
	{
		this.session = session;
	}

}
