package io.nadron.codecs.impl 
{
	import flash.utils.ByteArray;
	import io.nadron.codecs.Transform;
	import io.nadron.communication.MessageBuffer;
	import io.nadron.event.NadEvent;
	/**
	 * Converts an incoming NadEvent into a ByteArray. It will read the event type and 
	 * the source of the event and write them to a ByteArray.
	 * @author Abraham Menacherry
	 */
	public class MessageBufferEventEncoder implements Transform
	{
		import io.nadron.event.Events;
		
		public function MessageBufferEventEncoder() 
		{
			
		}
		
		public function transform(input:Object):Object
		{
			var event:NadEvent = input as NadEvent;
			var message:ByteArray = new ByteArray();
			var opCode:int = event.getType();
			message.writeByte(opCode);
			if (opCode == Events.LOG_IN) {
				message.writeByte(Events.JET_PROTOCOL);
			}
			var messageBuffer:MessageBuffer = event.getSource() as MessageBuffer
			message.writeBytes(messageBuffer.getBuffer());
			return message;
		}
	}

}