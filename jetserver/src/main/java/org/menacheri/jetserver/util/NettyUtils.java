package org.menacheri.jetserver.util;

import java.net.InetSocketAddress;
import java.util.NoSuchElementException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.menacheri.jetserver.app.PlayerSession;
import org.menacheri.jetserver.communication.NettyTCPMessageSender;
import org.menacheri.jetserver.convert.Transform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class would be an assortment of netty related utility methods.
 * @author Abraham Menacherry
 *
 */
public class NettyUtils
{
	private static final Logger LOG = LoggerFactory.getLogger(NettyUtils.class);
	private static final StringDecoderWrapper STRING_DECODER = new StringDecoderWrapper();
	private static final StringEncoderWrapper STRING_ENCODER = new StringEncoderWrapper();
	
	public static final String NETTY_CHANNEL = "NETTY_CHANNEL";
	
	public static ChannelPipeline getPipeLineOfConnection(
			NettyTCPMessageSender messageSender)
	{
		Channel channel = messageSender.getChannel();
		ChannelPipeline pipeline = channel.getPipeline();
		return pipeline;
	}
	
	public static ChannelPipeline getPipeLineOfConnection(
			PlayerSession playerSession)
	{
		return getPipeLineOfConnection((NettyTCPMessageSender)playerSession.getTcpSender());
	}
	
	/**
	 * A utility method to clear the netty pipeline of all handlers.
	 * 
	 * @param pipeline
	 */
	public static void clearPipeline(ChannelPipeline pipeline)
	{
		try
		{
			int counter = 0;
			
			while (pipeline.getFirst() != null)
			{
				pipeline.removeFirst();
				counter++;
			}
			LOG.trace("Removed {} handlers from pipeline",counter);
		}
		catch (NoSuchElementException e)
		{
			// all elements removed.
		}
	}
	
	public static ChannelBuffer createBufferForOpcode(int opCode)
	{
		ChannelBuffer buffer = ChannelBuffers.buffer(1);
		buffer.writeByte(opCode);
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
		String[] strings = new String[numOfStrings]; 
		for(int i=0;i<numOfStrings;i++)
		{
			String theStr = readString(buffer);
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
		String readString = null;
		if (null != buffer && buffer.readableBytes() > 2)
		{
			int length = buffer.readUnsignedShort();
			readString = readString(buffer, length);
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
		ChannelBuffer stringBuffer = buffer.readSlice(length);
		String str = null;
		try
		{
			str = STRING_DECODER.decode(stringBuffer);
		}
		catch (Exception e)
		{
			LOG.error("Error occurred while trying to read string from buffer: {}",e);
		}
		return str;
//		char[] chars = new char[length];
//		for (int i = 0; i < length; i++)
//		{
//			chars[i] = buffer.readChar();
//		}
//		return new String(chars);
		
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
		ChannelBuffer buffer = null;
		for (String msg : msgs)
		{
			if (null == buffer)
			{
				buffer = writeString(msg);
			}
			else
			{
				ChannelBuffer theBuffer = writeString(msg);
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
	public static ChannelBuffer writeString(String msg) {
		ChannelBuffer buffer = null;
		try
		{
			ChannelBuffer stringBuffer = STRING_ENCODER.encode(msg);
			if(null != stringBuffer){
				int length = stringBuffer.readableBytes();
				ChannelBuffer lengthBuffer = ChannelBuffers.buffer(2);
				lengthBuffer.writeShort(length);
				buffer = ChannelBuffers.wrappedBuffer(lengthBuffer,stringBuffer);
			}
			
		}
		catch (Exception e)
		{
			LOG.error("Error occurred while trying to write string to buffer: {}",e);
		}
		return buffer;
	}
	
	public static <T,V> V readObject(ChannelBuffer buffer, Transform<ChannelBuffer, V> decoder)
	{
		int length = 0;
		if(null != buffer && buffer.readableBytes() > 2){
			length = buffer.readUnsignedShort();
		}else{
			return null;
		}
		ChannelBuffer objBuffer = buffer.readSlice(length);
		V obj = null;
		try{
			obj = decoder.convert(objBuffer);
		}catch(Exception e){
			LOG.error("Error occurred while trying to read object from buffer: {}",e);
		}
		return obj;
	}
	
	public static <V> ChannelBuffer writeObject(
			Transform<V, ChannelBuffer> converter, V object) {
		ChannelBuffer buffer = null;
		try {
			ChannelBuffer objectBuffer = converter.convert(object);
			int length = objectBuffer.readableBytes();
			ChannelBuffer lengthBuffer = ChannelBuffers.buffer(2);
			lengthBuffer.writeShort(length);
			buffer = ChannelBuffers.wrappedBuffer(lengthBuffer,
					objectBuffer);
		} catch (Exception e) {
			LOG.error("Error occurred while writing object to buffer: {}", e);
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
	public static InetSocketAddress readSocketAddress(ChannelBuffer buffer)
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
	
	public static ChannelBuffer writeSocketAddress(InetSocketAddress socketAddress)
	{
		String host = socketAddress.getHostName();
		int port = socketAddress.getPort();
		ChannelBuffer hostName = writeString(host);
		ChannelBuffer portNum = ChannelBuffers.buffer(4);
		portNum.writeInt(port);
		ChannelBuffer socketAddressBuffer = ChannelBuffers.wrappedBuffer(hostName,portNum);
		return socketAddressBuffer;
	}
	
	public static class StringDecoderWrapper extends StringDecoder
	{
		public String decode(ChannelBuffer buffer) throws Exception
		{
			String message = (String) super.decode(null, null, buffer);
			return message;
		}
	}
	
	public static class StringEncoderWrapper extends StringEncoder
	{
		protected ChannelBuffer encode(Object msg) throws Exception
		{
			ChannelBuffer strBuffer = (ChannelBuffer)super.encode(null, null, msg);
			return strBuffer;
		}
	}
	
	
}
