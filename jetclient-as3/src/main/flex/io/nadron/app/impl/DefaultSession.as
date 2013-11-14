package io.nadron.app.impl 
{
	import flash.events.EventDispatcher;
	import flash.events.ProgressEvent;
	import flash.utils.Dictionary;
	import io.nadron.codecs.CodecChain;
	import io.nadron.communication.MessageSender;
	import io.nadron.app.Session;
	import flash.events.Event;
	import io.nadron.protocol.Protocol;
	
	/**
	 * The default implementation of the session interface. This class is responsible
	 * for receiving and sending events. For receiving it uses the
	 * messageReceived method and for sending it uses the sendMessage method.
	 * 
	 * @author Abraham Menacherry
	 * 
	 */
	public class DefaultSession extends EventDispatcher implements Session 
	{
		/**
		 * session id
		 */
		private var id:Object;
		/**
		 * session parameters
		 */
		private var sessionAttributes:Dictionary;
		private var creationTime:Number;
		private var writeable:Boolean = false;
		private var messageSender:MessageSender;
		private var inCodecs:CodecChain;
		private var outCodecs:CodecChain;
		/**
		 * Life cycle variable to check if the session is shutting down. If it is,
		 * then no more incoming events will be accepted. TODO implementation pending.
		 */
		private var shutDown:Boolean = false;
		
		public function DefaultSession(id:Object) 
		{
			this.id = id;
			sessionAttributes = new Dictionary();
			var date:Date = new Date();
			this.creationTime = date.valueOf();
		}
		
		public function messageReceived(event:Event):void
		{
			var data:Object = inCodecs.transform(event);
			if (null == data) {
				return;
			}
			dispatchEvent(data as Event);
			// if there is anymore data in the socket, then force message received to be invoked again.
			if (messageSender.getSocket().bytesAvailable > 0) {
				messageSender.getSocket().dispatchEvent(new ProgressEvent(ProgressEvent.SOCKET_DATA));
			}
		}
		
		public function sendToServer(message:Object):void
		{
			messageSender.sendMessage(message);
		}
		
		public function getId():Object 
		{
			return id;
		}
		
		public function setId(id:Object):void 
		{
			this.id = id;
		}
		
		public function getAttribute(key:String):Object 
		{
			return sessionAttributes[key];
		}
		
		public function setAttribute(key:String, value:Object):void 
		{
			sessionAttributes[key] = value;
		}
		
		public function removeAttribute(key:String):void 
		{
			delete sessionAttributes[key];
		}
		
		public function getCreationTime():Number 
		{
			return creationTime;
		}
		
		public function setCreationTime(time:Number):void 
		{
			this.creationTime = creationTime;
		}
		
		public function isWriteable():Boolean 
		{
			return writeable;
		}
		
		public function setWriteable(writeable:Boolean = true):void 
		{
			this.writeable = writeable;
		}
		
		public function close():void 
		{
			setShuttingDown();
			getMessageSender().close();
		}
		
		public function isShuttingDown():Boolean 
		{
			return shutDown;
		}
		
		public function setShuttingDown(shutDown:Boolean = true):void 
		{
			this.shutDown = shutDown;
		}
		
		public function getMessageSender():MessageSender 
		{
			return messageSender;
		}
		
		public function setMessageSender(messageSender:MessageSender):void 
		{
			this.messageSender = messageSender;
		}
		
		public function getInCodecs():CodecChain
		{
			return inCodecs;
		}
		
		public function setInCodecs(inCodecs:CodecChain):void
		{
			this.inCodecs = inCodecs;
		}
		
		public function getOutCodecs():CodecChain
		{
			return outCodecs;
		}
		
		public function setOutCodecs(outCodecs:CodecChain):void
		{
			this.outCodecs = outCodecs;
		}
		
	}

}