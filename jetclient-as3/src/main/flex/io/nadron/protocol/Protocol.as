package io.nadron.protocol 
{
	import flash.net.Socket;
	import io.nadron.app.Session;
	import io.nadron.communication.MessageSender;
	
	/**
	 * A network protocol that is used to connect to remote nadron server. Implementaions will use 
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