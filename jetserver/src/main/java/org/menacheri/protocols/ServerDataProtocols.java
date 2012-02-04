package org.menacheri.protocols;

import org.jboss.netty.buffer.ChannelBuffer;
import org.menacheri.communication.IMessageBuffer;


/**
 * This enumeration defines all the available protocols that can be used for
 * communicating with the server.
 * 
 * @author Abraham Menacherry
 * 
 */
public enum ServerDataProtocols
{
	/**
	 * String communication is used when using XML socket class at the flash.
	 * client side. Generally this can be used only for chat applications.
	 */
	STRING(0), 
	/**
	 * This protocol defines AMF3 as a byte array being sent over the wire. Used
	 * by flash clients that use Socket class.
	 */
	AMF3(1),
	/**
	 * This protocol defines AMF3 that is base 64 and String encoded sent over
	 * the wire. Used by XMLSocket flash clients to send AMF3 data.
	 */
	AMF3_STRING(2),
	/**
	 * In this protocol, the AMF3 object will not contain the synchronization
	 * id. Instead each incoming message will carry an integer as part of the
	 * header which will be used to do synchronization with the existing state.
	 */
	AMF3_EXTERNAL_INT_SYNC(3),
	/**
	 * This protocol will read the input as an array of bytes, only the length
	 * field will be dropped from the incoming message. 
	 */
	SIMPLE_BYTE_ARRAY(4),
	/**
	 * A <a href="http://www.jboss.org/netty">Netty</a> specific protocol that
	 * provides the {@link ChannelBuffer} class as the output of decoding for
	 * GameHandlers.
	 */
	CHANNEL_BUFFER_PROTOCOL(5),
	/**
	 * A thin layer over CHANNEL_BUFFER_PROTOCOL protocol decoders and encoders
	 * deal with {@link IMessageBuffer} instance.
	 * 
	 */
	MESSAGE_BUFFER_PROTOCOL(6),
	
	SIMPLE_SGS_PROTOCOL(7),
	/**
	 * Not yet supported, but the general idea is port unification where more
	 * than one protocol will be used dynamically. Meaning each message sent
	 * would also contain the protocol information. Handlers would then be
	 * dynamically switched for each message.
	 */
	DYNAMIC(9);

	ServerDataProtocols(int value)
	{
	}

	/**
	 * Utility method that returns the integer representation of a protocol.
	 * 
	 * @param protocol
	 *            The protocol that needs to be converted to int.
	 * @return Returns an integer representation of the protocol.
	 */
	public static int getInt(ServerDataProtocols protocol)
	{
		switch (protocol)
		{
		case STRING:
			return 0;
		case AMF3:
			return 1;
		case AMF3_STRING:
			return 2;
		case AMF3_EXTERNAL_INT_SYNC:
			return 3;
		case SIMPLE_BYTE_ARRAY:
			return 4;
		case CHANNEL_BUFFER_PROTOCOL:
			return 5;
		case MESSAGE_BUFFER_PROTOCOL:
			return 6;
		case SIMPLE_SGS_PROTOCOL:
			return 7;
		case DYNAMIC:
			return 9;
		default:
			return -1;
		}
	}

	/**
	 * Method that takes an integer and returns a corresponding protocol enum
	 * object.
	 * 
	 * @param protocol
	 *            The protocol object that needs to be converted to int.
	 * @return Returns the protocol if a valid int is passed.
	 */
	public static ServerDataProtocols getProtocol(int protocol)
	{
		switch (protocol)
		{
		case 0:
			return STRING;
		case 1:
			return AMF3;
		case 2:
			return AMF3_STRING;
		case 3:
			return AMF3_EXTERNAL_INT_SYNC;
		case 4:
			return SIMPLE_BYTE_ARRAY;
		case 5:
			return CHANNEL_BUFFER_PROTOCOL;
		case 6:
			return MESSAGE_BUFFER_PROTOCOL;
		case 7:
			return SIMPLE_SGS_PROTOCOL;
		case 9:
			return DYNAMIC;
		default:
			return null;
		}
	}
	
}
