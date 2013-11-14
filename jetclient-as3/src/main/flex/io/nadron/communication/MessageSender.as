package io.nadron.communication 
{
	import flash.net.Socket;
	
	/**
	 * ...
	 * @author Abraham Menacherry
	 */
	public interface MessageSender 
	{
		function sendMessage(message:Object):void;
		function close():void;
		function getSocket():Socket;
	}
	
}