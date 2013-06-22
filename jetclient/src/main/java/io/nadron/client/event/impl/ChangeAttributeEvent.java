package io.nadron.client.event.impl;

public class ChangeAttributeEvent extends DefaultEvent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5257419644823465715L;
	private String key;
	private Object value;

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

}
