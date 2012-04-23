package org.menacheri.jetserver.protocols.impl;

import org.menacheri.jetserver.app.IPlayerSession;
import org.menacheri.jetserver.protocols.IProtocol;

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
