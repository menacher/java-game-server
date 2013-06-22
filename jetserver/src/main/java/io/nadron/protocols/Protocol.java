package io.nadron.protocols;

import io.nadron.app.PlayerSession;
import io.nadron.handlers.netty.LoginHandler;
import io.netty.channel.ChannelPipeline;



/**
 * This interface defines a protocol that needs to be applied while
 * communicating to the user session object. For the netty implementation, this
 * would mean that the protocol would be a series of handlers, decoders and
 * encoders added to the pipeline associated with this session to enable the
 * relevant protocol. For Example, the STRING protocol would add string encoder
 * and decoder to the pipeline. AMF3 would add the relevant serializer and
 * de-serializer to the pipeline.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface Protocol
{
	/**
	 * Return the string name of this protocol.
	 * 
	 * @return name of the protocol. This will be used in scenarios where a
	 *         custom protocol has been used.
	 */
	public String getProtocolName();

	/**
	 * The main method of this interface. For the Netty implementation, this
	 * will be used to add the handlers in the pipeline associated with this
	 * user session. For now, "configuration" only means adding of handlers.
	 * It is expected that the {@link LoginHandler} or whichever previous
	 * handler was handling the message has cleared up the
	 * {@link ChannelPipeline} object.
	 * 
	 * @param playerSession
	 *            The user session for which the protocol handlers need to be
	 *            set.
	 */
	public void applyProtocol(PlayerSession playerSession);
	
	/**
	 * This method delegates to the {@link #applyProtocol(PlayerSession)} method
	 * after clearing the pipeline based on the input flag.
	 * 
	 * @param playerSession
	 * @param clearExistingProtocolHandlers
	 *            Clears the pipeline of existing protocol handlers if set to
	 *            true.
	 */
	public void applyProtocol(PlayerSession playerSession,
			boolean clearExistingProtocolHandlers);
}
