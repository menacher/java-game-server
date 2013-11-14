package io.nadron.codecs.impl 
{
	import flash.utils.ByteArray;
	import io.nadron.codecs.Transform;
	import io.nadron.event.Events;
	import io.nadron.event.NadEvent;
	import io.nadron.communication.MessageBuffer;
	
	/**
	 * This decoder will convert an incoming ByteArray into a NadEvent. It will first read the opcode 
	 * byte to see which kind of event is coming from remote nadron server, say SESSION_MESSAGE and it will 
	 * read the payload. The opcode will be used to create appropriate event type and the payload will 
	 * be written to a MessageBuffer.
	 * @author Abraham Menacherry
	 */
	public class MessagBufferEventDecoder implements Transform 
	{
		
		public function MessagBufferEventDecoder() 
		{
			
		}
		
		/* INTERFACE io.nadron.codecs.Transform */
		
		public function transform(input:Object):Object 
		{
			var message:ByteArray = input as ByteArray;
			
			var eventType:int = message.readByte();
			if (eventType == Events.NETWORK_MESSAGE) 
			{
				eventType = Events.SESSION_MESSAGE;
			}
			var buffer:MessageBuffer = new MessageBuffer(message);
			var event:NadEvent = Events.event(eventType, buffer);
			return event;
		}
		
	}

}