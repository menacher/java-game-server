package org.menacheri.protocols;

import org.jboss.netty.channel.ChannelPipeline;
import org.menacheri.app.IPlayerSession;


/**
 * This service is used to set the protocol for data transfer to the server.
 * Depending on the incoming protocol from client, the handlers would be
 * modified accordingly.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IProtocolConfigurationService
{
	/**
	 * This method is a facade for other methods to this interface. This method
	 * will set the protocol on the {@link IPlayerSession} instance and then
	 * call all the rest of the methods needed for full protocol configuration
	 * sequentially.
	 * 
	 * @param protocolKey String representation of the protocol.
	 * @param playerSession The user's session to server.
	 */
	public void configureProtocol(String protocolKey,
			IPlayerSession playerSession) throws UnknownProtocolException;

	/**
	 * This overloaded method accepts the protocol enumeration. It It then
	 * configures handlers for the{@link IPlayerSession} object. For Netty
	 * implementation this is done by modifying the {@link ChannelPipeline}
	 * associated with the session. This method delegates the actual handler
	 * addition to the applyHandler method on the {@link IProtocol} reference.
	 * 
	 * @param protocol
	 *            The protocol type for which handlers need to be configured.
	 * @param playerSession
	 *            The user's session to server
	 * @return For Netty implementation it returns the {@link ChannelPipeline}
	 *         object.
	 */
	public Object configureHandlersForProtocol(IProtocol protocol,
			IPlayerSession playerSession);

}