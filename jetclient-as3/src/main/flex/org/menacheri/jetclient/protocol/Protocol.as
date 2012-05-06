package org.menacheri.jetclient.protocol 
{
	import flash.net.Socket;
	import org.menacheri.jetclient.app.Session;
	import org.menacheri.jetclient.communication.MessageSender;
	
	/**
	 * A network protocol that is used to connect to remote jetserver. Implementaions will use 
	 * appropriate encoders and decoders based on the protocol implemented by server.
	 * @author Abraham Menacherry
	 */
	public interface Protocol 
	{
		function applyProtocol(session:Session):void;
		function getName():String;
		function createMessageSender(socket:Socket):MessageSender;
	}
	
}