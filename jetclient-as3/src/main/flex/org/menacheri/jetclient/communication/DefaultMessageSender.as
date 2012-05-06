package org.menacheri.jetclient.communication 
{
	import flash.net.Socket;
	import flash.utils.ByteArray;
	import org.menacheri.jetclient.app.Session;
	import org.menacheri.jetclient.codecs.Transform;
	/**
	 * A class used by the session to send messages to the remote JetServer. It contains methods for writing 
	 * messages as well as closing socket. It also has responsibility to tranform incoming object to bytes using 
	 * codec chain before writing to socket.
	 * @author Abraham Menacherry
	 */
	public class DefaultMessageSender implements MessageSender
	{
		private var socket:Socket;
		private var transform:Transform;
		
		public function DefaultMessageSender(socket:Socket,transform:Transform) 
		{
			this.socket = socket;
			this.transform = transform;
		}
		
		public function sendMessage(message:Object):void 
		{
			if (null == message) {
				return;
			}
			var bytes:ByteArray = transform.transform(message) as ByteArray;
			socket.writeBytes(bytes);
			socket.flush();
		}
		
		public function close():void 
		{
			if (null != socket)
			{
				socket.close();
			}
		}
		
		
	}

}