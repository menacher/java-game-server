package io.nadron.codecs.impl 
{
	import flash.events.Event;
	import flash.net.Socket;
	import flash.utils.ByteArray;
	import io.nadron.codecs.Transform;
	
	/**
	 * A codec which can decode an incoming frame from remote jetserver based on the 
	 * length of the incoming bytes. Nadron server sends data in the form of <lenght 2 bytes>-<payload of message>. 
	 * This decoder will read the length of the payload, capture the payload and pass it on to 
	 * the next decoder in the chain as a ByteArray. If all the bytes are not available it will return <b>null</b>.
	 * @author Abraham Menacherry
	 */
	public class LengthFieldBasedFrameDecoder implements Transform 
	{
		private var lengthFieldLength:int;
		private var lengthRead:Boolean;
		private var length:int;
		private var message:ByteArray;
		private var currentReadLength:int;
		
		public function LengthFieldBasedFrameDecoder(lengthFieldLength:int = 2) 
		{
			lengthRead = false;
			length = 0;
			this.lengthFieldLength = lengthFieldLength;
			message = new ByteArray();
		}
		
		public function transform(input:Object):Object 
		{
			var event:Event = input as Event;
			var socket:Socket = event.currentTarget as Socket;
			if (lengthRead || socket.bytesAvailable >= lengthFieldLength)
			{
				if (!lengthRead) 
				{
					length = socket.readShort();
					lengthRead = true;
					message.clear();
					currentReadLength = 0;
				}
				
				var bytesAvailable:uint = socket.bytesAvailable;
				if(bytesAvailable > 0)
				{
					var lengthToRead:uint;
					// if not enough bytes to complete the message then read it into message array are return null
					if( (length - currentReadLength) > bytesAvailable)
					{
						lengthToRead = bytesAvailable;
						socket.readBytes(message, currentReadLength, lengthToRead);
						currentReadLength += lengthToRead;
						return null;
					}
					else//(length - currentReadLength) <= bytesAvailable
					{
						// enough or more bytes are available return message
						lengthToRead = (length - currentReadLength);
						socket.readBytes(message, currentReadLength, lengthToRead);
						lengthRead = false;
						length = 0;
						currentReadLength = 0;
						return message;
					}
				}
				else
				{
					return null;
				}
			} 
			else 
			{
				return null;
			}
		}
		
	}

}