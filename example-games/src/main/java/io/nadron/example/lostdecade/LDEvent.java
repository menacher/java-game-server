package io.nadron.example.lostdecade;

import io.nadron.event.impl.DefaultEvent;

public class LDEvent extends DefaultEvent
{
	private static final long serialVersionUID = 1L;

	private LDGameState source;
	
	@Override
	public LDGameState getSource()
	{
		return source;
	}
	
	public void setSource(LDGameState source)
	{
		this.source = source;
	}
}
