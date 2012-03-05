package org.menacheri.convert;

import org.menacheri.jetclient.communication.IMessageBuffer;

/**
 * A generic interface for transforming one object to another. Implementations
 * of this interface can be used for decoding and encoding objects, maybe while
 * reading from a {@link IMessageBuffer} or writing to a Netty channel.
 * 
 * @author Abraham Menacherry
 * 
 * @param <T>
 * @param <V>
 */
public interface ITransform<T, V>
{

	/**
	 * Convert Object of type T to type V.
	 * 
	 * @param object
	 *            The incoming object, mostly a buffer or byte array.
	 * @return Returns the converted object.
	 * @throws Exception
	 */
	V convert(T object) throws Exception;
}
