package io.nadron.util 
{
	import io.nadron.communication.MessageBuffer;
	import io.nadron.event.NadEvent;
	import io.nadron.event.Events;
	import flash.utils.ByteArray;
	
	/**
	 * ...
	 * @author Abraham Menacherry
	 */
	public class LoginHelper 
	{
		private var username:String;
		private var password:String;
		private var connectionKey:Object;
		private var remoteHost:String;
		private var remotePort:int;
		
		public function LoginHelper(username:String, password:String, connectionKey:Object,
						remoteHost:String,remotePort:int = 18090) 
		{
			this.username = username;
			this.password = password;
			this.connectionKey = connectionKey;
			this.remoteHost = remoteHost;
			this.remotePort = remotePort;
		}
		
		public function getLoginEvent():NadEvent
		{
			var loginBuffer:MessageBuffer = new MessageBuffer(new ByteArray());
			loginBuffer.writeMultiStrings(username, password, connectionKey);
			return Events.event(Events.LOG_IN, loginBuffer);
		}
		
		public function getRemoteHost():String
		{
			return remoteHost;
		}
		
		public function getRemotePort():int
		{
			return remotePort;
		}
	}

}