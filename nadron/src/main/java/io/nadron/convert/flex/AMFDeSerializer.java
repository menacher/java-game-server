package io.nadron.convert.flex;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import flex.messaging.io.SerializationContext;
import flex.messaging.io.amf.Amf3Input;

/**
 * This class is used to convert an AMF3 object (received as a byte array) to a
 * Java object. It uses AMF3Input and SerializationContext classes to achieve
 * this functionality.
 * 
 * @author Abraham Menacherry
 * 
 */
public class AMFDeSerializer {

	/**
	 * The serialization context used as an input for the AMF3Input class.
	 */
	private SerializationContext context;

	public AMFDeSerializer(SerializationContext serializationContext) {
		this.context = serializationContext;
	}

	/**
	 * This method takes an AMF3 object in byte array form and converts it to a
	 * corresponding java object.
	 * 
	 * @param <T>
	 * @param amf
	 *            The serialized AMF3 object as a byte array.
	 * @return Returns the java object after conversion.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public <T> T fromAmf(final ByteArrayInputStream amf)
			throws ClassNotFoundException, IOException {
		Amf3Input amf3Input = new Amf3Input(context);
		amf3Input.setInputStream(amf);
		// Read object does the actual work of conversion.
		T object = (T) amf3Input.readObject();
		amf3Input.close();
		return object;
	}

	public SerializationContext getContext() {
		return context;
	}

	public void setContext(SerializationContext context) {
		this.context = context;
	}

}
