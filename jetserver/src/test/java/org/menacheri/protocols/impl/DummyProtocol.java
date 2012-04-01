package org.menacheri.protocols.impl;

import org.menacheri.app.IPlayerSession;
import org.menacheri.protocols.IProtocol;

public class DummyProtocol implements IProtocol 
{
	@Override
	public String getProtocolName()
	{
		return null;
	}

	@Override
	public void applyProtocol(IPlayerSession playerSession)
	{

	}
}
