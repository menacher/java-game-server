package org.menacheri.jetclient.codecs.impl 
{
	import flash.utils.ByteArray;
	import org.menacheri.jetclient.codecs.Transform;
	import org.menacheri.jetclient.event.JetEvent;
	
	/**
	 * Converts an incoming JetEvent into a ByteArray. It will read the event type from the  
	 * JetEvent. Then it will serialize the payload an object to byte array. Both the event 
	 * type and serialized bytes are written to a byte array and send to next encoder in the chain.
	 * @author Abraham Menacherry
	 */
	public class AMFSerializer implements Transform
	{
		
		public function AMFSerializer() 
		{
			
		}
		
		public function transform(input:Object):Object
		{
			if(input == null){
				throw new Error("null isn't a legal serialization candidate");
			}
			var jetEvent:JetEvent = input as JetEvent;
			var bytes:ByteArray = new ByteArray();
			bytes.writeByte(jetEvent.getType());
			bytes.writeObject(jetEvent.getSource());
			bytes.position = 0;
			return bytes;
		}
	}

}