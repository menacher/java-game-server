package org.menacheri.jetclient.handlers 
{
	import flash.events.Event;
	import flash.events.ProgressEvent;
	import flash.net.Socket;
	import org.menacheri.jetclient.app.impl.DefaultSession;
	import org.menacheri.jetclient.app.Session;
	import org.menacheri.jetclient.codecs.CodecChain;
	import org.menacheri.jetclient.codecs.InAndOutCodecChain;
	import org.menacheri.jetclient.codecs.Transform;
	import org.menacheri.jetclient.event.Events;
	import org.menacheri.jetclient.event.JetEvent;
	
	/**
	 * Used for logging in to the remote jetserver. It will basically wait till the START event is sent 
	 * by remote JetServer and then set the messageReceived function on the session and the event 
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
			var jetEvent:JetEvent = loginCodecs.getInCodecs().transform(event) as JetEvent;
			if (null == jetEvent) {
				// Decoding is not over, mostly because the whole frame  
				// was not received by the LengthFieldBasedFrameDecoder.
				return;
			}
			if (jetEvent.getType() == Events.START) {
				trace("Start Event received");
				var socket:Socket = event.currentTarget as Socket;
				socket.removeEventListener(ProgressEvent.SOCKET_DATA, this.handleLogin);
				socket.addEventListener(ProgressEvent.SOCKET_DATA, session.messageReceived);
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