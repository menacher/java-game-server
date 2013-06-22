package io.nadron.protocols.impl;

import io.nadron.app.PlayerSession;
import io.nadron.protocols.Protocol;

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
