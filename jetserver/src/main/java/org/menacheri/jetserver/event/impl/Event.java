package org.menacheri.jetserver.event.impl;

import java.io.Serializable;

import org.menacheri.jetserver.event.IEvent;
import org.menacheri.jetserver.event.IEventContext;


public class Event implements IEvent, Serializable
{
	/**
	 * Eclipse Generated serial version id.
	 */
	private static final long serialVersionUID = 8188757584720622237L;
	
	private IEventContext eventContext;
	private int type;
	private Object source;
	private long timeStamp;
	
	@Override
	public IEventContext getEventContext()
	{
		return eventContext;
	}

	@Override
	public int getType()
	{
		return type;
	}

	@Override
	public Object getSource()
	{
		return source;
	}

	@Override
	public long getTimeStamp()
	{
		return timeStamp;
	}

	@Override
	public void setEventContext(IEventContext context)
	{
		this.eventContext = context;
	}

	@Override
	public void setType(int type)
	{
		this.type = type;
	}

	@Override
	public void setSource(Object source)
	{
		this.source = source;
	}

	@Override
	public void setTimeStamp(long timeStamp)
	{
		this.timeStamp = timeStamp;

	}

	@Override
	public String toString() {
		return "Event [type=" + type + ", source=" + source + ", timeStamp="
				+ timeStamp + "]";
	}
	
}
