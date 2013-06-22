package io.nadron.convert.flex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import flex.messaging.io.SerializationContext;
import flex.messaging.io.amf.Amf3Output;

/**
 * This class is used to serialize java objects to AMF3 binary format. It uses
 * AMF3 output class from blazeds library for this purpose.
 * 
 * @author Abraham Menacherry
 * 
 */
public class AMFSerializer
{
	/**
	 * Used by the blazeds api for its context.
	 */
	private SerializationContext context;

	/**
	 * This constructor is used by the PictureDataEncoder class in order to
	 * create an instance of this class and later use it for converting the java
	 * object to AMF3.
	 * 
	 * @param serializationContext
	 */
	public AMFSerializer(SerializationContext serializationContext)
	{
		this.context = serializationContext;
	}

	/**
	 * Method used to convert the java object to AMF3 format. This method in
	 * turn delegates this conversion to the blazeds class AMF3 output.
	 * 
	 * @param <T>
	 * @param source
	 *            This is the java object that is to be converted to AMF3.
	 * @return Returns the byte array output stream which was created by
	 *         serializing a java object to AMF3.
	 * @throws IOException
	 */
	public <T> ByteArrayOutputStream toAmf(final T source) throws IOException
	{
		// Create and instance of the byte array output stream to write the AMF3
		// object to.
		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		// Create the AMF3Output object and set its output stream.
		final Amf3Output amf3Output = new Amf3Output(context);
		amf3Output.setOutputStream(bout);
		// Write the AMF3 object to the byte array output stream after
		// converting the source java object.
		amf3Output.writeObject(source);
		// Flush and close the stream.
		amf3Output.flush();
		amf3Output.close();
		return bout;
	}

	public SerializationContext getContext()
	{
		return context;
	}

	public void setContext(SerializationContext context)
	{
		this.context = context;
	}
}
