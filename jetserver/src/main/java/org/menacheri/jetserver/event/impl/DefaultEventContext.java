package org.menacheri.jetserver.event.impl;

import org.menacheri.jetserver.app.Session;
import org.menacheri.jetserver.event.EventContext;


public class DefaultEventContext implements EventContext
{

	private Object attachement;
	private Session session;

	@Override
	public Object getAttachment()
	{
		return attachement;
	}

	@Override
	public Session getSession()
	{
		return session;
	}

	@Override
	public void setAttachment(Object attachement)
	{
		this.attachement = attachement;
	}

	@Override
	public void setSession(Session session)
	{
		this.session = session;
	}

}
