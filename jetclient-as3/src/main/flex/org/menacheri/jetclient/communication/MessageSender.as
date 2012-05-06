package org.menacheri.jetclient.communication 
{
	
	/**
	 * ...
	 * @author Abraham Menacherry
	 */
	public interface MessageSender 
	{
		function sendMessage(message:Object):void;
		function close():void;
	}
	
}