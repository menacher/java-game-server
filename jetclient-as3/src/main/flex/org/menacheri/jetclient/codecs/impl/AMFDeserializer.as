package org.menacheri.jetclient.codecs.impl 
{
	import flash.utils.ByteArray;
	import org.menacheri.jetclient.codecs.Transform;
	import org.menacheri.jetclient.event.Events;
	
	/**
	 * This decoder will convert an incoming ByteArray into a JetEvent. It will first read the opcode 
	 * byte to see which kind of event is coming from remote jetserver, say SESSION_MESSAGE and it will 
	 * read the payload into an object using byte array's readObject method.
	 * @author Abraham Menacherry
	 */
	public class AMFDeserializer implements Transform
	{
		
		public function AMFDeserializer() 
		{
			
		}
	
		public function transform(input:Object):Object
		{
			var bytes:ByteArray = input as ByteArray;
			var eventType:int = bytes.readByte();
			if (eventType == Events.NETWORK_MESSAGE) 
			{
				eventType = Events.SESSION_MESSAGE;
			}
			return Events.event(eventType, bytes.readObject());
		}
		
	}

}