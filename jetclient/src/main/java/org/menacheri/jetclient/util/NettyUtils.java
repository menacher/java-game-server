package org.menacheri.jetclient.util;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.NoSuchElementException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;
import org.jboss.netty.util.CharsetUtil;
import org.menacheri.convert.Transform;

/**
 * This class would be an assortment of netty related utility methods.
 * 
 * @author Abraham Menacherry
 * 
 */
public class NettyUtils
{
	private static final ObjectDecoderWrapper OBJECT_DECODER = new ObjectDecoderWrapper();

	public static final String NETTY_CHANNEL = "NETTY_CHANNEL";

	/**
	 * A utility method to clear the netty pipeline of all handlers.
	 * 
	 * @param pipeline
	 */
	public static void clearPipeline(ChannelPipeline pipeline)
			throws NoSuchElementException
	{
		while (pipeline.getFirst() != null)
		{
			pipeline.removeFirst();
		}
	}

	public static ChannelBuffer createBufferForOpcode(int opcode)
	{
		ChannelBuffer buffer = ChannelBuffers.buffer(1);
		buffer.writeByte(opcode);
		return buffer;
	}

	/**
	 * This method will read multiple strings of the buffer and return them as a
	 * string array. It internally uses the readString(ChannelBuffer buffer) to
	 * accomplish this task. The strings are read back in the order they are
	 * written.
	 * 
	 * @param buffer
	 *            The buffer containing the strings, with each string being a
	 *            strlength-strbytes combination.
	 * @param numOfStrings
	 *            The number of strings to be read. Should not be negative or 0
	 * @return the strings read from the buffer as an array.
	 */
	public static String[] readStrings(ChannelBuffer buffer, int numOfStrings)
	{
		return readStrings(buffer,numOfStrings,CharsetUtil.UTF_8);
	}

	/**
	 * This method will read multiple strings of the buffer and return them as a
	 * string array. It internally uses the readString(ChannelBuffer buffer) to
	 * accomplish this task. The strings are read back in the order they are
	 * written.
	 * 
	 * @param buffer
	 *            The buffer containing the strings, with each string being a
	 *            strlength-strbytes combination.
	 * @param numOfStrings
	 *            The number of strings to be read. Should not be negative or 0
	 * @param charset
	 *            The Charset say 'UTF-8' in which the decoding needs to be
	 *            done.
	 * 
	 * @return the strings read from the buffer as an array.
	 */
	public static String[] readStrings(ChannelBuffer buffer, int numOfStrings,
			Charset charset)
	{
		String[] strings = new String[numOfStrings]; 
		for(int i=0;i<numOfStrings;i++)
		{
			String theStr = readString(buffer,charset);
			if(null == theStr) break;
			strings[i] = theStr;
		}
		return strings;
	}
	
	/**
	 * This method will first read an unsigned short to find the length of the
	 * string and then read the actual string based on the length. This method
	 * will also reset the reader index to end of the string
	 * 
	 * @param buffer
	 *            The Netty buffer containing at least one unsigned short
	 *            followed by a string of similar length.
	 * @return Returns the String or throws {@link IndexOutOfBoundsException} if
	 *         the length is greater than expected.
	 */
	public static String readString(ChannelBuffer buffer)
	{
		return readString(buffer, CharsetUtil.UTF_8);
	}

	/**
	 * This method will first read an unsigned short to find the length of the
	 * string and then read the actual string based on the length. This method
	 * will also reset the reader index to end of the string
	 * 
	 * @param buffer
	 *            The Netty buffer containing at least one unsigned short
	 *            followed by a string of similar length.
	 * @param charset
	 *            The Charset say 'UTF-8' in which the decoding needs to be
	 *            done.
	 * @return Returns the String or throws {@link IndexOutOfBoundsException} if
	 *         the length is greater than expected.
	 */
	public static String readString(ChannelBuffer buffer, Charset charset)
	{
		String readString = null;
		if (null != buffer && buffer.readableBytes() > 2)
		{
			int length = buffer.readUnsignedShort();
			readString = readString(buffer, length, charset);
		}
		return readString;
	}
	
	/**
	 * Read a string from a channel buffer with the specified length. It resets
	 * the reader index of the buffer to the end of the string.
	 * 
	 * @param buffer
	 *            The Netty buffer containing the String.
	 * @param length
	 *            The number of bytes in the String.
	 * @return Returns the read string.
	 */
	public static String readString(ChannelBuffer buffer, int length)
	{
		return readString(buffer, length, CharsetUtil.UTF_8);
	}

	/**
	 * Read a string from a channel buffer with the specified length. It resets
	 * the reader index of the buffer to the end of the string. Defaults to
	 * UTF-8 encoding in case charset passed in is null
	 * 
	 * @param buffer
	 *            The Netty buffer containing the String.
	 * @param length
	 *            The number of bytes in the String.
	 * @param charset
	 *            The Charset say 'UTF-8' in which the decoding needs to be
	 *            done.
	 * @return Returns the read string.
	 */
	public static String readString(ChannelBuffer buffer, int length,
			Charset charset)
	{
		String str = null;
		if (null == charset)
		{
			charset = CharsetUtil.UTF_8;
		}
		try
		{
			ChannelBuffer stringBuffer = buffer.readSlice(length);
			str = stringBuffer.toString(charset);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		return str;
	}
	
	/**
	 * Writes multiple strings to a channelBuffer with the length of the string
	 * preceding its content. So if there are two string <code>Hello</code> and
	 * <code>World</code> then the channel buffer returned would contain <Length
	 * of Hello><Hello as UTF-8 binary><Length of world><World as UTF-8 binary>
	 * 
	 * @param msgs
	 *            The messages to be written.
	 * @return {@link ChannelBuffer} with format
	 *         length-stringbinary-length-stringbinary
	 */
	public static ChannelBuffer writeStrings(String... msgs)
	{
		return writeStrings(CharsetUtil.UTF_8, msgs);
	}

	/**
	 * Writes multiple strings to a channelBuffer with the length of the string
	 * preceding its content. So if there are two string <code>Hello</code> and
	 * <code>World</code> then the channel buffer returned would contain <Length
	 * of Hello><Hello as appropriate charset binary><Length of world><World as
	 * UTF-8 binary>
	 * 
	 * @param charset
	 *            The Charset say 'UTF-8' in which the encoding needs to be
	 *            done.
	 * @param msgs
	 *            The messages to be written.
	 * @return {@link ChannelBuffer} with format
	 *         length-stringbinary-length-stringbinary
	 */
	public static ChannelBuffer writeStrings(Charset charset, String... msgs)
	{
		ChannelBuffer buffer = null;
		for (String msg : msgs)
		{
			if (null == buffer)
			{
				buffer = writeString(msg, charset);
			}
			else
			{
				ChannelBuffer theBuffer = writeString(msg,charset);
				if(null != theBuffer)
				{
					buffer = ChannelBuffers.wrappedBuffer(buffer,theBuffer);
				}
			}
		}
		return buffer;
	}
	/**
	 * Creates a channel buffer of which the first 2 bytes contain the length of
	 * the string in bytes and the remaining is the actual string in binary
	 * UTF-8 format.
	 * 
	 * @param msg
	 *            The string to be written.
	 * @return Returns the ChannelBuffer instance containing the encoded string
	 */
	public static ChannelBuffer writeString(String msg)
	{
		return writeString(msg, CharsetUtil.UTF_8);
	}

	/**
	 * Creates a channel buffer of which the first 2 bytes contain the length of
	 * the string in bytes and the remaining is the actual string in binary with
	 * specified format. Defaults to UTF-8 encoding in case charset passed in is
	 * null
	 * 
	 * @param msg
	 *            The string to be written.
	 * @param charset
	 *            The Charset say 'UTF-8' in which the encoding needs to be
	 *            done.
	 * @return The Netty channel buffer containing the string encoded as bytes
	 *         in the provided charset. It will return <code>null</code> if the
	 *         string parameter is null.
	 */
	public static ChannelBuffer writeString(String msg, Charset charset) 
	{
		ChannelBuffer buffer = null;
		try
		{
			ChannelBuffer stringBuffer = null;
			if (null == charset)
			{
				charset = CharsetUtil.UTF_8;
			}
			stringBuffer = copiedBuffer(ByteOrder.BIG_ENDIAN, msg, charset);
			int length = stringBuffer.readableBytes();
			ChannelBuffer lengthBuffer = ChannelBuffers.buffer(2);
			lengthBuffer.writeShort(length);
			buffer = ChannelBuffers.wrappedBuffer(lengthBuffer, stringBuffer);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		return buffer;
	}
	
	/**
	 * This method will read multiple objects of the buffer and return them as
	 * an object array. It internally uses the readObject(ChannelBuffer buffer)
	 * to accomplish this task. The objects are read back in the order they are
	 * written.
	 * 
	 * @param buffer
	 *            The buffer containing the objects, with each object being a
	 *            objlength-objbytes combination.
	 * @param numOfObjects
	 *            The number of objects to be read. Should not be negative or 0
	 * @return the objects read from the buffer as an array.
	 */
	public static Object[] readObjects(ChannelBuffer buffer, int numOfObjects)
	{
		Object[] objects = new String[numOfObjects];
		for (int i = 0; i < numOfObjects; i++)
		{
			Object theObject = readObject(buffer);
			if (null == theObject)
				break;
			objects[i] = theObject;
		}
		return objects;
	}

	/**
	 * This method will first read an unsigned short to find the length of the
	 * object and then read the actual object based on the length. It sets the
	 * reader index of the buffer to current reader index + 2(length bytes) +
	 * actual length.
	 * 
	 * @param buffer
	 *            The Netty buffer containing at least one unsigned short
	 *            followed by an Object of that length.
	 * @return Returns the String or throws {@link IndexOutOfBoundsException} if
	 *         the length is greater than expected.
	 */
	public static Object readObject(ChannelBuffer buffer)
	{
		Object readObj = null;
		if (null != buffer && buffer.readableBytes() > 2)
		{
			int length = buffer.readUnsignedShort();
			readObj = readObject(buffer, length);
		}
		return readObj;
	}

	/**
	 * Read an object from a channel buffer with the specified length. It sets
	 * the reader index of the buffer to current reader index + 2(length bytes)
	 * + actual length.
	 * 
	 * @param buffer
	 *            The Netty buffer containing the Object.
	 * @param length
	 *            The number of bytes in the Object.
	 * @return Returns the read object.
	 */
	public static Object readObject(ChannelBuffer buffer, int length)
	{
		ChannelBuffer objBuffer = buffer.readSlice(length);
		Object obj;
		try
		{
			obj = OBJECT_DECODER.decode(objBuffer);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		return obj;
	}

	public static <T, V> V readObject(ChannelBuffer buffer,
			Transform<ChannelBuffer, V> decoder)
	{
		int length = 0;
		if (null != buffer && buffer.readableBytes() > 2)
		{
			length = buffer.readUnsignedShort();
		}
		else
		{
			return null;
		}
		ChannelBuffer objBuffer = buffer.readSlice(length);
		V obj;
		try
		{
			obj = decoder.convert(objBuffer);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		;
		return obj;
	}

	public static <V> ChannelBuffer writeObject(
			Transform<V, ChannelBuffer> converter, V object)
	{
		ChannelBuffer buffer = null;
		ChannelBuffer objectBuffer;
		try
		{
			objectBuffer = converter.convert(object);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		int length = objectBuffer.readableBytes();
		ChannelBuffer lengthBuffer = ChannelBuffers.buffer(2);
		lengthBuffer.writeShort(length);
		buffer = ChannelBuffers.wrappedBuffer(lengthBuffer, objectBuffer);
		return buffer;
	}

	/**
	 * Read a socket address from a buffer. The socket address will be provided
	 * as two strings containing host and port.
	 * 
	 * @param buffer
	 *            The buffer containing the host and port as string.
	 * @return The InetSocketAddress object created from host and port or null
	 *         in case the strings are not there.
	 */
	public static InetSocketAddress readSocketAddress(ChannelBuffer buffer)
			throws Exception
	{
		String remoteHost = NettyUtils.readString(buffer);
		int remotePort = 0;
		if (buffer.readableBytes() >= 4)
		{
			remotePort = buffer.readInt();
		}
		else
		{
			return null;
		}
		InetSocketAddress remoteAddress = null;
		if (null != remoteHost)
		{
			remoteAddress = new InetSocketAddress(remoteHost, remotePort);
		}
		return remoteAddress;
	}

	public static ChannelBuffer writeSocketAddress(
			InetSocketAddress socketAddress)
	{
		String host = socketAddress.getHostName();
		int port = socketAddress.getPort();
		ChannelBuffer hostName = writeString(host);
		ChannelBuffer portNum = ChannelBuffers.buffer(4);
		portNum.writeInt(port);
		ChannelBuffer socketAddressBuffer = ChannelBuffers.wrappedBuffer(
				hostName, portNum);
		return socketAddressBuffer;
	}

	public static class ObjectDecoderWrapper extends ObjectDecoder
	{
		public ObjectDecoderWrapper()
		{
			super(ClassResolvers.weakCachingResolver(null));
		}

		public Object decode(ChannelBuffer buffer) throws Exception
		{
			return super.decode(null, null, buffer);
		}
	}

	public static class ObjectEncoderWrapper extends ObjectEncoder
	{
		protected ChannelBuffer encode(Object msg) throws Exception
		{
			ChannelBuffer objBuffer = (ChannelBuffer) super.encode(null, null,
					msg);
			return objBuffer;
		}
	}
}
