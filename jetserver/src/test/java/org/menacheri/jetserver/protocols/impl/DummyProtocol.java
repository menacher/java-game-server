package org.menacheri.jetserver.protocols.impl;

import org.menacheri.jetserver.app.PlayerSession;
import org.menacheri.jetserver.protocols.Protocol;

public class DummyProtocol implements Protocol 
{
	@Override
	public String getProtocolName()
	{
		return null;
	}

	@Override
	public void applyProtocol(PlayerSession playerSession)
	{

	}

	@Override
	public void applyProtocol(PlayerSession playerSession,
			boolean clearExistingProtocolHandlers) {
		
	}
}
