package io.nadron.communication;

import io.nadron.convert.Transform;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;



/**
 * A message buffer can be used for communicating complex messages between
 * server and client or between sessions. It is a read-destroy buffer meaning,
 * that once a value is read off it, it cannot be read again as the internal
 * pointers would have moved to the post-read position. This buffer could hold
 * almost any sort of data in binary form <code>int</code>, <code>String</code>,
 * other native types, <code>byte[]</code> of serialized objects and so on. It
 * contains helper methods for writing and reading back the data. If a Netty
 * implementation is used, then it would be a wrapper over the
 * {@link ByteBuf}. For Java api, it would be probably be wrapper over a
 * nio {@link ByteBuffer}.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface MessageBuffer<T> {
	/**
	 * @return Returns true if something can be read from this buffer, else
	 *         false.
	 */
	boolean isReadable();

	/**
	 * Gets the number of readable bytes left in the buffer.
	 * 
	 * @return an integer containing the remaining readable bytes.
	 */
	int readableBytes();

	/**
	 * Read a single signed byte from the current {@code readerIndex} position
	 * of the buffer. It will increment the readerIndex after doing this
	 * operation.
	 * 
	 * @return Returns the byte that is read
	 * @throws IndexOutOfBoundsException
	 *             if isReadable() returns false.
	 */
	int readByte();

	byte[] readBytes(int length);

	/**
	 * Transfers this buffer's data to the specified destination starting at the
	 * current {@code readerIndex} and increases the {@code readerIndex} by the
	 * number of the transferred bytes (= {@code dst.length}).
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code dst.length} is greater than
	 *             {@code this.readableBytes}
	 */
	void readBytes(byte[] dst);

	/**
	 * Transfers this buffer's data to the specified destination starting at the
	 * current {@code readerIndex} and increases the {@code readerIndex} by the
	 * number of the transferred bytes (= {@code length}).
	 * 
	 * @param dstIndex
	 *            the first index of the destination
	 * @param length
	 *            the number of bytes to transfer
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the specified {@code dstIndex} is less than {@code 0}, if
	 *             {@code length} is greater than {@code this.readableBytes}, or
	 *             if {@code dstIndex + length} is greater than
	 *             {@code dst.length}
	 */
	void readBytes(byte[] dst, int dstIndex, int length);

	/**
	 * Gets an unsigned byte at the current {@code readerIndex} and increases
	 * the {@code readerIndex} by {@code 1} in this buffer.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code this.readableBytes} is less than {@code 1}
	 */
	int readUnsignedByte();

	/**
	 * Gets a 16-bit short integer at the current {@code readerIndex} and
	 * increases the {@code readerIndex} by {@code 2} in this buffer.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code this.readableBytes} is less than {@code 2}
	 */
	int readShort();

	/**
	 * Gets an unsigned 16-bit short integer at the current {@code readerIndex}
	 * and increases the {@code readerIndex} by {@code 2} in this buffer.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code this.readableBytes} is less than {@code 2}
	 */
	int readUnsignedShort();

	/**
	 * Gets a 24-bit medium integer at the current {@code readerIndex} and
	 * increases the {@code readerIndex} by {@code 3} in this buffer.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code this.readableBytes} is less than {@code 3}
	 */
	int readMedium();

	/**
	 * Gets an unsigned 24-bit medium integer at the current {@code readerIndex}
	 * and increases the {@code readerIndex} by {@code 3} in this buffer.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code this.readableBytes} is less than {@code 3}
	 */
	int readUnsignedMedium();

	/**
	 * Gets a 32-bit integer at the current {@code readerIndex} and increases
	 * the {@code readerIndex} by {@code 4} in this buffer.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code this.readableBytes} is less than {@code 4}
	 */
	int readInt();

	/**
	 * Gets an unsigned 32-bit integer at the current {@code readerIndex} and
	 * increases the {@code readerIndex} by {@code 4} in this buffer.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code this.readableBytes} is less than {@code 4}
	 */
	long readUnsignedInt();

	/**
	 * Gets a 64-bit integer at the current {@code readerIndex} and increases
	 * the {@code readerIndex} by {@code 8} in this buffer.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code this.readableBytes} is less than {@code 8}
	 */
	long readLong();

	/**
	 * Gets a 2-byte UTF-16 character at the current {@code readerIndex} and
	 * increases the {@code readerIndex} by {@code 2} in this buffer.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code this.readableBytes} is less than {@code 2}
	 */
	char readChar();

	/**
	 * Gets a 32-bit floating point number at the current {@code readerIndex}
	 * and increases the {@code readerIndex} by {@code 4} in this buffer.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code this.readableBytes} is less than {@code 4}
	 */
	float readFloat();

	/**
	 * Gets a 64-bit floating point number at the current {@code readerIndex}
	 * and increases the {@code readerIndex} by {@code 8} in this buffer.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code this.readableBytes} is less than {@code 8}
	 */
	double readDouble();

	String readString();

	String[] readStrings(int numOfStrings);

	/**
	 * Reads an object from the underlying buffer and transform the bytes using
	 * the supplied transformer to any desired object. This method provide the
	 * flexibility to decode the bytes to any type of object.
	 * 
	 * @param converter
	 *            The converter which will transform the bytes to relevant
	 *            object.
	 * @return The object of type V, or null if the underlying buffer is null or
	 *         empty.
	 */
	<V> V readObject(Transform<T, V> converter);

	MessageBuffer<T> writeByte(byte b);

	/**
	 * Transfers the specified source array's data to this buffer starting at
	 * the current {@code writerIndex} and increases the {@code writerIndex} by
	 * the number of the transferred bytes (= {@code src.length}).
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code src.length} is greater than
	 *             {@code this.writableBytes}
	 */
	MessageBuffer<T> writeBytes(byte[] src);

	/**
	 * Sets the specified 16-bit short integer at the current
	 * {@code writerIndex} and increases the {@code writerIndex} by {@code 2} in
	 * this buffer. The 16 high-order bits of the specified value are ignored.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code this.writableBytes} is less than {@code 2}
	 */
	MessageBuffer<T> writeShort(int value);

	/**
	 * Sets the specified 24-bit medium integer at the current
	 * {@code writerIndex} and increases the {@code writerIndex} by {@code 3} in
	 * this buffer.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code this.writableBytes} is less than {@code 3}
	 */
	MessageBuffer<T> writeMedium(int value);

	/**
	 * Sets the specified 32-bit integer at the current {@code writerIndex} and
	 * increases the {@code writerIndex} by {@code 4} in this buffer.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code this.writableBytes} is less than {@code 4}
	 */
	MessageBuffer<T> writeInt(int value);

	/**
	 * Sets the specified 64-bit long integer at the current {@code writerIndex}
	 * and increases the {@code writerIndex} by {@code 8} in this buffer.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code this.writableBytes} is less than {@code 8}
	 */
	MessageBuffer<T> writeLong(long value);

	/**
	 * Sets the specified 2-byte UTF-16 character at the current
	 * {@code writerIndex} and increases the {@code writerIndex} by {@code 2} in
	 * this buffer. The 16 high-order bits of the specified value are ignored.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code this.writableBytes} is less than {@code 2}
	 */
	MessageBuffer<T> writeChar(int value);

	/**
	 * Sets the specified 32-bit floating point number at the current
	 * {@code writerIndex} and increases the {@code writerIndex} by {@code 4} in
	 * this buffer.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code this.writableBytes} is less than {@code 4}
	 */
	MessageBuffer<T> writeFloat(float value);

	/**
	 * Sets the specified 64-bit floating point number at the current
	 * {@code writerIndex} and increases the {@code writerIndex} by {@code 8} in
	 * this buffer.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code this.writableBytes} is less than {@code 8}
	 */
	MessageBuffer<T> writeDouble(double value);

	MessageBuffer<T> writeString(String message);

	MessageBuffer<T> writeStrings(String... message);

	/**
	 * Most implementations will write an object to the underlying buffer after
	 * converting the incoming object using the transformer into a byte array.
	 * This method provides the flexibility to encode any type of object, to a
	 * byte array or buffer(mostly).
	 * 
	 * @param converter
	 *            For most implementations, the converter which will transform
	 *            the object to byte array.
	 * @param <V>
	 *            The object to be converted, mostly to a byte array or relevant
	 *            buffer implementation.
	 * @return Instance of this class itself.
	 */
	<V> MessageBuffer<T> writeObject(Transform<V, T> converter, V object);

	/**
	 * Returns the actual buffer implementation that is wrapped in this
	 * MessageBuffer instance.
	 * 
	 * @return This method will return the underlying buffer. For Netty that
	 *         would be a {@link ByteBuf}, for a core java implementation
	 *         it could be {@link ByteBuffer}
	 */
	T getNativeBuffer();

	/**
	 * Returns the backing byte array of this buffer.
	 * 
	 * @throws UnsupportedOperationException
	 *             if there no accessible backing byte array
	 */
	byte[] array();

	/**
	 * Clears the contents of this buffer.
	 */
	void clear();
}
