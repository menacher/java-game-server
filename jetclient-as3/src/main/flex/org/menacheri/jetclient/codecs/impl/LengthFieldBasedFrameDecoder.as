package org.menacheri.jetclient.codecs.impl 
{
	import flash.events.Event;
	import flash.net.Socket;
	import flash.utils.ByteArray;
	import org.menacheri.jetclient.codecs.Transform;
	
	/**
	 * A codec which can decode an incoming frame from remote jetserver based on the 
	 * length of the incoming bytes. Jetserver sends data in the form of <lenght 2 bytes>-<payload of message>. 
	 * This decoder will read the length of the payload, capture the payload and pass it on to 
	 * the next decoder in the chain as a ByteArray. If all the bytes are not available it will return <b>null</b>.
	 * @author Abraham Menacherry
	 */
	public class LengthFieldBasedFrameDecoder implements Transform 
	{
		private var lengthFieldLength:int;
		private var lengthRead:Boolean;
		private var length:int;
		
		public function LengthFieldBasedFrameDecoder(lengthFieldLength:int = 2) 
		{
			lengthRead = false;
			length = 0;
			this.lengthFieldLength = lengthFieldLength;
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
				}
				
				if (socket.bytesAvailable >= length) 
				{
					// reset the frame flags. So that next frame can be read.
					lengthRead = false;
					length = 0;
					var message:ByteArray = new ByteArray();
					socket.readBytes(message, 0, length);
					return message;
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