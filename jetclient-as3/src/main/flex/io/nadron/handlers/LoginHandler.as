package io.nadron.handlers 
{
	import flash.events.Event;
	import flash.events.ProgressEvent;
	import flash.net.Socket;
	import io.nadron.app.impl.DefaultSession;
	import io.nadron.app.Session;
	import io.nadron.codecs.CodecChain;
	import io.nadron.codecs.InAndOutCodecChain;
	import io.nadron.codecs.Transform;
	import io.nadron.event.Events;
	import io.nadron.event.NadEvent;
	
	/**
	 * Used for logging in to the remote Nadron server. It will basically wait till the START event is sent 
	 * by remote Nadron Server and then set the messageReceived function on the session and the event 
	 * listener for future socket communication.
	 * @author Abraham Menacherry
	 */
	public class LoginHandler
	{
		private var session:Session;
		private var loginCodecs:InAndOutCodecChain;
				
		public function LoginHandler(session:Session, loginCodecs:InAndOutCodecChain) 
		{
			this.session = session;
			this.loginCodecs = loginCodecs;
		}
		
		public function handleLogin(event:Event):void 
		{
			var nEvent:NadEvent = loginCodecs.getInCodecs().transform(event) as NadEvent;
			if (null == nEvent) {
				// Decoding is not over, mostly because the whole frame  
				// was not received by the LengthFieldBasedFrameDecoder.
				return;
			}
			var socket:Socket = event.currentTarget as Socket;
			if(nEvent.getType() == Events.LOG_IN_SUCCESS || nEvent.getType() == Events.GAME_ROOM_JOIN_SUCCESS)
			{
				trace("Log in success/Game room join success Event received: " + nEvent.getType());
				
				if (socket.bytesAvailable > 0)
				{
					//force the socket to read GAME_ROOM_JOIN_SUCCESS
					socket.dispatchEvent(new ProgressEvent(ProgressEvent.SOCKET_DATA));
				}
			}
			else if (nEvent.getType() == Events.START) {
				trace("Start Event received");
				
				socket.removeEventListener(ProgressEvent.SOCKET_DATA, this.handleLogin);
				socket.addEventListener(ProgressEvent.SOCKET_DATA, session.messageReceived);
				if (socket.bytesAvailable > 0) 
				{
					//check if socket received message from the server and force it to read the message
					socket.dispatchEvent(new ProgressEvent(ProgressEvent.SOCKET_DATA));
				}
			}
		}
		
		public function getLoginCodecs():InAndOutCodecChain
		{
			return loginCodecs;
		}
		
		public function setLoginCodecs(loginCodecs:InAndOutCodecChain):void
		{
			this.loginCodecs = loginCodecs;
		}
		
	}

}