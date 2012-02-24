package org.menacheri.communication;

import java.io.Serializable;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.DynamicChannelBuffer;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;
import org.menacheri.convert.ITransform;
import org.menacheri.util.NettyUtils;


/**
 * This class is an implementation of the {@link IMessageBuffer} interface. It is a thin
 * wrapper over the the Netty {@link ChannelBuffer} with some additional methods
 * for string and object read write. It does not expose all methods of the
 * ChannelBuffer, instead it has a method {@link #getNativeBuffer()} which can
 * be used to retrieve the buffer and then call the appropriate method. For
 * writing to the buffer, this class uses {@link DynamicChannelBuffer}
 * implementation.
 * 
 * @author Abraham Menacherry
 * 
 */
public class NettyMessageBuffer implements IMessageBuffer<ChannelBuffer>
{
	private final ChannelBuffer buffer;

	public NettyMessageBuffer()
	{
		buffer = ChannelBuffers.dynamicBuffer();
	}

	/**
	 * This constructor can be used when trying to read information from a
	 * {@link ChannelBuffer}.
	 * 
	 * @param buffer
	 */
	public NettyMessageBuffer(ChannelBuffer buffer)
	{
		this.buffer = buffer;
	}

	@Override
	public boolean isReadable()
	{
		return buffer.readable();
	}

	@Override
	public int readableBytes()
	{
		return buffer.readableBytes();
	}
	
	@Override
	public byte[] array()
	{
		return buffer.array();
	}

	@Override
	public void clear()
	{
		buffer.clear();
	}

	@Override
	public ChannelBuffer getNativeBuffer()
	{
		return buffer;
	}

	@Override
	public int readByte()
	{
		return buffer.readByte();
	}

	@Override
	public int readUnsignedByte()
	{
		return buffer.readUnsignedByte();
	}

	@Override
	public byte[] readBytes(int length)
	{
		byte[] bytes = new byte[length];
		buffer.readBytes(bytes);
		return bytes;
	}

	@Override
	public void readBytes(byte[] dst)
	{
		buffer.readBytes(dst);
	}

	@Override
	public void readBytes(byte[] dst, int dstIndex, int length)
	{
		buffer.readBytes(dst, dstIndex, length);
	}

	@Override
	public char readChar()
	{
		return buffer.readChar();
	}

	@Override
	public int readUnsignedShort()
	{
		return buffer.readUnsignedShort();
	}

	@Override
	public int readShort()
	{
		return buffer.readShort();
	}

	@Override
	public int readUnsignedMedium()
	{
		return buffer.readUnsignedMedium();
	}

	@Override
	public int readMedium()
	{
		return buffer.readMedium();
	}

	@Override
	public long readUnsignedInt()
	{
		return buffer.readUnsignedInt();
	}

	@Override
	public int readInt()
	{
		return buffer.readInt();
	}

	@Override
	public long readLong()
	{
		return buffer.readLong();
	}

	@Override
	public float readFloat()
	{
		return buffer.readFloat();
	}

	@Override
	public double readDouble()
	{
		return buffer.readChar();
	}

	@Override
	public String readString()
	{
		return NettyUtils.readString(buffer);
	}

	@Override
	public String[] readStrings(int numOfStrings)
	{
		return NettyUtils.readStrings(buffer, numOfStrings);
	}

	/**
	 * Used to read an object from the buffer. Note that the encoder used to
	 * write the object in the first place must be Netty {@link ObjectEncoder}.
	 * To read objects written in using other encoders like say AMF3, use the
	 * {@link #readObject(ITransform)} method instead.
	 * 
	 * @return The object that is read from the underlying {@link ChannelBuffer}
	 */
	public Object readObject()
	{
		return NettyUtils.readObject(buffer);
	}

	/**
	 * Used to read a specified number of objects from the buffer. Note that the
	 * encoder used to write the object in the first place must be Netty
	 * {@link ObjectEncoder}. To read objects written in using other encoders
	 * like say AMF3, use the {@link #readObject(ITransform)} method instead.
	 * 
	 * @param numOfObjects
	 *            The number of objects to be read from the underlying
	 *            {@link ChannelBuffer}
	 * @return The object that is read from the underlying {@link ChannelBuffer}
	 */
	public Object[] readObjects(int numOfObjects) 
	{
		return NettyUtils.readObjects(buffer, numOfObjects);
	}

	public <V> V readObject(ITransform<ChannelBuffer,V> converter)
	{
		return NettyUtils.readObject(buffer, converter);
	}
		
	@Override
	public IMessageBuffer<ChannelBuffer> writeByte(byte b)
	{
		buffer.writeByte(b);
		return this;
	}

	@Override
	public IMessageBuffer<ChannelBuffer> writeBytes(byte[] src)
	{
		buffer.writeBytes(src);
		return this;
	}

	@Override
	public IMessageBuffer<ChannelBuffer> writeChar(int value)
	{
		buffer.writeChar(value);
		return this;
	}

	@Override
	public IMessageBuffer<ChannelBuffer> writeShort(int value)
	{
		buffer.writeShort(value);
		return this;
	}

	@Override
	public IMessageBuffer<ChannelBuffer> writeMedium(int value)
	{
		buffer.writeMedium(value);
		return this;
	}

	@Override
	public IMessageBuffer<ChannelBuffer> writeInt(int value)
	{
		buffer.writeInt(value);
		return this;
	}

	@Override
	public IMessageBuffer<ChannelBuffer> writeLong(long value)
	{
		buffer.writeLong(value);
		return this;
	}

	@Override
	public IMessageBuffer<ChannelBuffer> writeFloat(float value)
	{
		buffer.writeFloat(value);
		return this;
	}

	@Override
	public IMessageBuffer<ChannelBuffer> writeDouble(double value)
	{
		buffer.writeDouble(value);
		return this;
	}

	@Override
	public IMessageBuffer<ChannelBuffer> writeString(String message)
	{
		ChannelBuffer strBuf = NettyUtils.writeString(message);
		buffer.writeBytes(strBuf);
		return this;
	}

	@Override
	public IMessageBuffer<ChannelBuffer> writeStrings(String... messages)
	{
		ChannelBuffer strMultiBuf = NettyUtils.writeStrings(messages);
		buffer.writeBytes(strMultiBuf);
		return this;
	}

	/**
	 * Writes a serializable java object to the underlying {@link ChannelBuffer}
	 * . This uses Netty specific serialization, hence the client which decodes
	 * this object should also be using Netty decoder
	 * 
	 * @param serializable
	 * @return Returns the same buffer instance. Can be used for easy chained
	 *         message creation.
	 */
	public IMessageBuffer<ChannelBuffer> writeObject(Serializable serializable)
	{
		ChannelBuffer objBuf = NettyUtils.writeObject(serializable);
		buffer.writeBytes(objBuf);
		return this;
	}

	/**
	 * Writes a serializable java object to the underlying {@link ChannelBuffer}
	 * . This uses Netty specific serialization, hence the client which decodes
	 * this object should also be using Netty decoder
	 * 
	 * @param serializable
	 * @return Returns the same buffer instance. Can be used for easy chained
	 *         message creation.
	 */
	public IMessageBuffer<ChannelBuffer> writeObjects(Serializable... serializable)
	{
		ChannelBuffer objBuf = NettyUtils.writeObjects(serializable);
		buffer.writeBytes(objBuf);
		return this;
	}

	@Override
	public <V> IMessageBuffer<ChannelBuffer> writeObject(
			ITransform<V, ChannelBuffer> converter, V object) {
		ChannelBuffer objBuf = NettyUtils.writeObject(converter, object);
		buffer.writeBytes(objBuf);
		return this;
	}

}
