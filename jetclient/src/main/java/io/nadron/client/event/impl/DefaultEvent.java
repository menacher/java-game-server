package io.nadron.client.event.impl;

import io.nadron.client.event.Event;

import java.io.Serializable;


/**
 * Instances of this class are used to hold events that come in from remote
 * nadron server, for communicating between sessions and also for transmitting
 * messages to the remote nadron server from client.
 * 
 * @author Abraham Menacherry
 * 
 */
public class DefaultEvent implements Event, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1114679476675012101L;

	protected int type;
	protected Object source;
	protected long timeStamp;

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
	public String toString()
	{
		return "Event [type=" + type + ", source=" + source + ", timeStamp="
				+ timeStamp + "]";
	}

}
