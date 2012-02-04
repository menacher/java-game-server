package org.menacheri.service.impl;

import org.menacheri.app.IGameRoom;
import org.menacheri.app.IPlayerSession;
import org.menacheri.handlers.netty.HandshakeHandler;
import org.menacheri.protocols.ServerDataProtocols;
import org.menacheri.service.IHandshakeService;
import org.menacheri.service.ILookupService;
import org.menacheri.util.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * This class is the default implementation for the handshake service. It is
 * used mainly by the {@link HandshakeHandler} instance for completing handshake
 * with client.
 * 
 * @author Abraham Menacherry
 * 
 */
public class HandshakeService implements IHandshakeService
{
	private static final Logger LOG = LoggerFactory.getLogger(HandshakeService.class);
	private ILookupService lookupService;
	
	public HandshakeService()
	{

	}

	@Override
	public IPlayerSession validateCredentialsAndCreateSession(Object refKey)
	{
		IGameRoom gameRoom = lookupService.gameRoomLookup(refKey);
		if (null == gameRoom)
		{
			return null;
		}

		return gameRoom.createPlayerSession();
	}

	@Override
	public String generateAck(IPlayerSession playerSession)
	{
		String expectedAck = ServerDataProtocols
				.getInt(ServerDataProtocols.AMF3_STRING)
				+ RandomStringGenerator.generateRandomString(7);
		return expectedAck;
	}

	@Override
	public boolean validateAck(IPlayerSession playerSession,
			String incomingAck, String expectedAck)
	{
		LOG.trace("IncomingAck: {} ExpectedAck: {}",incomingAck, expectedAck);
		if (null == incomingAck)
			return false;

		// If both strings are equal return true.
		if (incomingAck.equals(expectedAck))
			return true;
		// If only the first two characters are different, return true. Since
		// first
		// character is a protocol signaler. The second character is for future
		// use if we support more than 10 different protocols in future.
		else if (incomingAck.substring(2).equals(expectedAck.substring(2)))
			return true;
		// else return false.
		else
			return false;
	}

	public ILookupService getLookupService()
	{
		return lookupService;
	}

	@Required
	public void setLookupService(ILookupService lookupService)
	{
		this.lookupService = lookupService;
	}
	
}
