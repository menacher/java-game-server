package org.menacheri.util;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.NoSuchElementException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.menacheri.app.IPlayerSession;
import org.menacheri.app.ISession;
import org.menacheri.communication.NettyTCPMessageSender;
import org.menacheri.convert.ITransform;
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
	private static final ObjectDecoderWrapper OBJECT_DECODER = new ObjectDecoderWrapper();
	private static final ObjectEncoderWrapper OBJECT_ENCODER = new ObjectEncoderWrapper();
	
	public static final String NETTY_CHANNEL = "NETTY_CHANNEL";
	
	public static ChannelPipeline getPipeLineOfConnection(
			NettyTCPMessageSender messageSender)
	{
		Channel channel = messageSender.getChannel();
		ChannelPipeline pipeline = channel.getPipeline();
		return pipeline;
	}
	
	public static ChannelPipeline getPipeLineOfConnection(
			IPlayerSession playerSession)
	{
		Channel channel = (Channel)playerSession.getConnectParameter(NETTY_CHANNEL);
		if(null != channel)
		{
			return channel.getPipeline();
		}
		else
		{
			return null;
		}
	}
	
	public static Channel getChannelOfSession(ISession session)
	{
		return (Channel)session.getConnectParameter(NETTY_CHANNEL);
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
		for(int i=0;i<numOfObjects;i++)
		{
			Object theObject = readObject(buffer);
			if(null == theObject) break;
			objects[i] = theObject;
		}
		return objects;
	}
	
	/**
	 * This method will first read an unsigned short to find the length of the
	 * object and then read the actual object based on the length. It sets
	 * the reader index of the buffer to current reader index + 2(length bytes)
	 * + actual length.
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
		Object obj = null;
		try{
			obj = OBJECT_DECODER.decode(objBuffer);
		}catch(Exception e){
			LOG.error("Error occurred while trying to read string from buffer: {}",e);
		}
		return obj;
	}

	public static <T,V> V readObject(ChannelBuffer buffer, ITransform<ChannelBuffer, V> decoder)
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
	
	/**
	 * Writes a collection of objects to a channel buffer and returns the
	 * channel buffer. Each object will be written in the format. This method
	 * will internall use writeObject(Object message) method.
	 * objlength.objbytes.
	 * 
	 * @param messages
	 *            The collection of objects to be written to the
	 *            {@link ChannelBuffer}
	 * @return The {@link ChannelBuffer} created from writing all these
	 *         messages.
	 */
	public static ChannelBuffer writeObjects(Serializable... messages)
	{
		ChannelBuffer buffer = null;
		for (Serializable msg : messages)
		{
			if (null == buffer)
			{
				buffer = writeObject(msg);
			}
			else
			{
				ChannelBuffer theBuffer = writeObject(msg);
				if(null != theBuffer)
				{
					buffer = ChannelBuffers.wrappedBuffer(buffer,theBuffer);
				}
			}
		}
		return buffer;
	}
	
	/**
	 * This method will write an Object to a {@link ChannelBuffer} in the format
	 * objlength.objbytes and return that buffer.
	 * 
	 * @param message
	 *            The message to be written.
	 * @return The {@link ChannelBuffer} created from writing this message.
	 */
	public static ChannelBuffer writeObject(Serializable message) {
		ChannelBuffer buffer = null;
		try {
			ChannelBuffer objectBuffer = OBJECT_ENCODER.encode(message);
			if (null != objectBuffer) {
				int length = objectBuffer.readableBytes();
				ChannelBuffer lengthBuffer = ChannelBuffers.buffer(2);
				lengthBuffer.writeShort(length);
				buffer = ChannelBuffers.wrappedBuffer(lengthBuffer,
						objectBuffer);
			}

		} catch (Exception e) {
			LOG.error("Error occurred while writing object to buffer: {}", e);
		}
		return buffer;
	}

	public static <V> ChannelBuffer writeObject(
			ITransform<V, ChannelBuffer> converter, V object) {
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
	
	public static class ObjectDecoderWrapper extends ObjectDecoder
	{
		public Object decode(ChannelBuffer buffer) throws Exception
		{
			return super.decode(null, null, buffer);
		}
	}
	
	public static class ObjectEncoderWrapper extends ObjectEncoder
	{
		protected ChannelBuffer encode(Object msg) throws Exception
		{
			ChannelBuffer objBuffer = (ChannelBuffer)super.encode(null, null, msg);
			return objBuffer;
		}
	}
}
