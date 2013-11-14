package io.nadron.codecs.impl 
{
	import flash.utils.ByteArray;
	import io.nadron.codecs.Transform;
	import io.nadron.event.NadEvent;
	
	/**
	 * Converts an incoming NadEvent into a ByteArray. It will read the event type from the  
	 * NadEvent. Then it will serialize the payload an object to byte array. Both the event 
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
			var nEvent:NadEvent = input as NadEvent;
			var bytes:ByteArray = new ByteArray();
			bytes.writeByte(nEvent.getType());
			bytes.writeObject(nEvent.getSource());
			bytes.position = 0;
			return bytes;
		}
	}

}