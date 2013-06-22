package io.nadron.convert.flex;

import flex.messaging.io.SerializationContext;

/**
 * This class provides threadlocal contexts on demand to the serializer and
 * deserializer class. This context object is necessary for blazeds to do
 * serialization.
 * 
 * @author Abraham Menacherry
 * 
 */
public class SerializationContextProvider {

	public SerializationContext get()
	{
		// Threadlocal SerializationContent
		SerializationContext serializationContext = SerializationContext
				.getSerializationContext();
		serializationContext.enableSmallMessages = true;
		serializationContext.instantiateTypes = true;
		// use _remoteClass field
		serializationContext.supportRemoteClass = true;
		// false Legacy Flex 1.5 behavior was to return a java.util.Collection
		// for Array, New Flex 2+ behavior is to return Object[] for AS3 Array
		serializationContext.legacyCollection = false;
		// false Legacy flash.xml.XMLDocument Type
		serializationContext.legacyMap = false;
		// true New E4X XML Type
		serializationContext.legacyXMLDocument = false;
		// determines whether the constructed Document is name-space aware
		serializationContext.legacyXMLNamespaces = false;
		serializationContext.legacyThrowable = false;
		serializationContext.legacyBigNumbers = false;
		serializationContext.restoreReferences = false;
		serializationContext.logPropertyErrors = false;
		serializationContext.ignorePropertyErrors = true;
		return serializationContext;
	}
}
