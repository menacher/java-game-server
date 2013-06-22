package io.nadron.event.impl;

import io.nadron.event.Events;

public class ChangeAttributeEvent extends DefaultEvent
{
	private static final long serialVersionUID = -5257419644823465715L;
	private String key;
	private Object value;

	public ChangeAttributeEvent(String key, Object value)
	{
		this.key = key;
		this.value = value;
	}
	
	@Override
	public int getType() {
		return Events.CHANGE_ATTRIBUTE;
	}
	
	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
		this.setSource(value);
	}

	@Override
	public String toString() {
		return "ChangeAttributeEvent [key=" + key + ", value=" + value
				+ ", type=" + type + "]";
	}

}
