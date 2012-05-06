package org.menacheri.jetclient.codecs.impl 
{
	import flash.utils.ByteArray;
	import org.menacheri.jetclient.codecs.Transform;
	import org.menacheri.jetclient.event.Events;
	import org.menacheri.jetclient.event.JetEvent;
	import org.menacheri.jetclient.communication.MessageBuffer;
	
	/**
	 * This decoder will convert an incoming ByteArray into a JetEvent. It will first read the opcode 
	 * byte to see which kind of event is coming from remote jetserver, say SESSION_MESSAGE and it will 
	 * read the payload. The opcode will be used to create appropriate event type and the payload will 
	 * be written to a MessageBuffer.
	 * @author Abraham Menacherry
	 */
	public class MessagBufferEventDecoder implements Transform 
	{
		
		public function MessagBufferEventDecoder() 
		{
			
		}
		
		/* INTERFACE org.menacheri.jetclient.codecs.Transform */
		
		public function transform(input:Object):Object 
		{
			var message:ByteArray = input as ByteArray;
			
			var eventType:int = message.readByte();
			if (eventType == Events.NETWORK_MESSAGE) 
			{
				eventType = Events.SESSION_MESSAGE;
			}
			var buffer:MessageBuffer = new MessageBuffer(message);
			var event:JetEvent = Events.event(eventType, buffer);
			return event;
		}
		
	}

}