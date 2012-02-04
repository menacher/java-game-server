package org.menacheri.protocols;

import org.jboss.netty.channel.ChannelPipeline;
import org.menacheri.app.IPlayerSession;
import org.menacheri.handlers.netty.LoginHandler;


/**
 * This interface defines a protocol that needs to be applied while
 * communicating to the user session object. For the netty implementation,
 * this would mean that the protocol would be a series of handlers, decoders and
 * encoders added to the pipeline associated with this session to enable the
 * relevant protocol. For Example, the STRING protocol would add string encoder
 * and decoder to the pipeline. AMF3 would add the relevant serializer and
 * de-serializer to the pipeline.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IProtocol
{
	/**
	 * Return the string name of this protocol.
	 * 
	 * @return name of the protocol. This will be used in scenarios where a
	 *         custom protocol has been used.
	 */
	public String getProtocolName();

	/**
	 * This method returns the associated protocol enumeration. Not very useful
	 * if you are implementing custom protocols.
	 * 
	 * @return Some protocols are supported by default and this enumeration
	 *         lists them.
	 */
	public ServerDataProtocols getProtocolEnum();

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
	public void applyProtocol(IPlayerSession playerSession);
}
