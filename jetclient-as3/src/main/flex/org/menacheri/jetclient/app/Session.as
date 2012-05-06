package org.menacheri.jetclient.app 
{
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	import org.menacheri.jetclient.codecs.CodecChain;
	import org.menacheri.jetclient.communication.MessageSender;
	import org.menacheri.jetclient.event.JetEvent;
	import flash.events.Event;
	import org.menacheri.jetclient.codecs.InAndOutCodecChain;
	
	/**
	* This interface abstracts a session in jetclient. A session can be thought of as a high 
	* level connection to a remote jetserver over TCP. The session also has event dispatching
	* capabilities. So when an event comes into the session, it will get dispatched to the
	* appropriate event listener.
	* 
	* @author Abraham Menacherry
	* 
	*/
	public interface Session extends IEventDispatcher
	{
		function messageReceived(event:Event):void;
		function sendToServer(message:Object):void;
		function getId():Object;
		function setId(id:Object):void;
		function getAttribute(key:String):Object;
		function setAttribute(key:String, value:Object):void;
		function removeAttribute(key:String):void; 
		function getCreationTime():Number;
		function setCreationTime(time:Number):void 
		function isWriteable():Boolean;
		function setWriteable(writeable:Boolean = true):void;
		function close():void;
		function isShuttingDown():Boolean;
		function setShuttingDown(shutDown:Boolean = true):void;
		function getMessageSender():MessageSender;
		function setMessageSender(messageSender:MessageSender):void;
		function getInCodecs():CodecChain; 
		function setInCodecs(inCodecs:CodecChain):void 
		function getOutCodecs():CodecChain; 
		function setOutCodecs(outCodecs:CodecChain):void;
	}
	
}