package io.nadron.codecs.impl 
{
	import flash.utils.ByteArray;
	import io.nadron.codecs.Transform;
	
	/**
	 * Whenever a message is sent to remote nadron server the length of the number of bytes 
	 * needs to be prepended to the message. This encoder will do that. It will accept a 
	 * byte array, find its length, create another byte array of the form <length><orginial byte array> 
	 * and return it. Normally this is the last encoder before a message is written to a socket.
	 * @author Abraham Menacherry
	 */
	public class LengthFieldPrepender implements Transform
	{
			
		public function LengthFieldPrepender() 
		{
			
		}
		
		public function transform(input:Object):Object
		{
			var message:ByteArray = input as ByteArray;
			var buffer:ByteArray = new ByteArray;
			buffer.writeShort(message.length);
			buffer.writeBytes(message);
			return buffer;
		}
	}

}