package io.nadron.client.util;

import static io.netty.buffer.Unpooled.copiedBuffer;
import io.nadron.client.app.Session;
import io.nadron.client.communication.NettyTCPMessageSender;
import io.nadron.convert.Transform;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.NoSuchElementException;


/**
 * This class would be an assortment of netty related utility methods.
 * 
 * @author Abraham Menacherry
 * 
 */
public class NettyUtils
{
	public static final String NETTY_CHANNEL = "NETTY_CHANNEL";

	public static ChannelPipeline getPipeLineOfConnection(
			NettyTCPMessageSender messageSender)
	{
		if(null != messageSender){
	        return messageSender.getChannel().pipeline();
	    }
	    return null;
	}
	
	public static ChannelPipeline getPipeLineOfSession(
			Session playerSession)
	{
		return getPipeLineOfConnection((NettyTCPMessageSender)playerSession.getTcpMessageSender());
	}
	
	/**
	 * A utility method to clear the netty pipeline of all handlers.
	 * 
	 * @param pipeline
	 */
	public static void clearPipeline(ChannelPipeline pipeline)
	{
		if(null == pipeline){
			return;
		}
		try
		{
			while (pipeline.first() != null)
			{
				pipeline.removeFirst();
			}
		}
		catch (NoSuchElementException e)
		{
			// all elements removed.
		}
	}

	public static ByteBuf createBufferForOpcode(int opcode)
	{
		ByteBuf buffer = Unpooled.buffer(1);
		buffer.writeByte(opcode);
		return buffer;
	}

	/**
	 * This method will read multiple strings of the buffer and return them as a
	 * string array. It internally uses the readString(ByteBuf buffer) to
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
	public static String[] readStrings(ByteBuf buffer, int numOfStrings)
	{
		return readStrings(buffer,numOfStrings,CharsetUtil.UTF_8);
	}

	/**
	 * This method will read multiple strings of the buffer and return them as a
	 * string array. It internally uses the readString(ByteBuf buffer) to
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
	public static String[] readStrings(ByteBuf buffer, int numOfStrings,
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
	public static String readString(ByteBuf buffer)
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
	public static String readString(ByteBuf buffer, Charset charset)
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
	public static String readString(ByteBuf buffer, int length)
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
	public static String readString(ByteBuf buffer, int length,
			Charset charset)
	{
		String str = null;
		if (null == charset)
		{
			charset = CharsetUtil.UTF_8;
		}
		try
		{
			ByteBuf stringBuffer = buffer.readSlice(length);
			str = stringBuffer.toString(charset);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		return str;
	}
	
	/**
	 * Writes multiple strings to a ByteBuf with the length of the string
	 * preceding its content. So if there are two string <code>Hello</code> and
	 * <code>World</code> then the channel buffer returned would contain <Length
	 * of Hello><Hello as UTF-8 binary><Length of world><World as UTF-8 binary>
	 * 
	 * @param msgs
	 *            The messages to be written.
	 * @return {@link ByteBuf} with format
	 *         length-stringbinary-length-stringbinary
	 */
	public static ByteBuf writeStrings(String... msgs)
	{
		return writeStrings(CharsetUtil.UTF_8, msgs);
	}

	/**
	 * Writes multiple strings to a ByteBuf with the length of the string
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
	 * @return {@link ByteBuf} with format
	 *         length-stringbinary-length-stringbinary
	 */
	public static ByteBuf writeStrings(Charset charset, String... msgs)
	{
		ByteBuf buffer = null;
		for (String msg : msgs)
		{
			if (null == buffer)
			{
				buffer = writeString(msg, charset);
			}
			else
			{
				ByteBuf theBuffer = writeString(msg,charset);
				if(null != theBuffer)
				{
					buffer = Unpooled.wrappedBuffer(buffer,theBuffer);
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
	 * @return Returns the ByteBuf instance containing the encoded string
	 */
	public static ByteBuf writeString(String msg)
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
	public static ByteBuf writeString(String msg, Charset charset) 
	{
		ByteBuf buffer = null;
		try
		{
			ByteBuf stringBuffer = null;
			if (null == charset)
			{
				charset = CharsetUtil.UTF_8;
			}
			stringBuffer = copiedBuffer(msg, charset);
			int length = stringBuffer.readableBytes();
			ByteBuf lengthBuffer = Unpooled.buffer(2);
			lengthBuffer.writeShort(length);
			buffer = Unpooled.wrappedBuffer(lengthBuffer, stringBuffer);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		return buffer;
	}
	
	/**
	 * Read an object from a channel buffer with the specified length. It sets
	 * the reader index of the buffer to current reader index + 2(length bytes)
	 * + actual length.
	 * 
	 * @param buffer
	 *            The Netty buffer containing the Object.
	 * @return Returns the read object.
	 */
	public static <T,V> V readObject(ByteBuf buffer, Transform<ByteBuf, V> decoder)
	{
		int length = 0;
		if(null != buffer && buffer.readableBytes() > 2)
		{
			length = buffer.readUnsignedShort();
		}
		else
		{
			return null;
		}
		ByteBuf objBuffer = buffer.readSlice(length);
		V obj = null;
		try{
			obj = decoder.convert(objBuffer);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		return obj;
	}
	
	public static <V> ByteBuf writeObject(
			Transform<V, ByteBuf> converter, V object) 
	{
		ByteBuf buffer = null;
		try 
		{
			ByteBuf objectBuffer = converter.convert(object);
			int length = objectBuffer.readableBytes();
			ByteBuf lengthBuffer = Unpooled.buffer(2);
			lengthBuffer.writeShort(length);
			buffer = Unpooled.wrappedBuffer(lengthBuffer,
					objectBuffer);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
	public static InetSocketAddress readSocketAddress(ByteBuf buffer)
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

	public static ByteBuf writeSocketAddress(
			InetSocketAddress socketAddress)
	{
		String host = socketAddress.getHostName();
		int port = socketAddress.getPort();
		ByteBuf hostName = writeString(host);
		ByteBuf portNum = Unpooled.buffer(4);
		portNum.writeInt(port);
		ByteBuf socketAddressBuffer = Unpooled.wrappedBuffer(
				hostName, portNum);
		return socketAddressBuffer;
	}

}
