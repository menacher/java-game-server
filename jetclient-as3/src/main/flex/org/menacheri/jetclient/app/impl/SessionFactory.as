package org.menacheri.jetclient.app.impl 
{
	import flash.events.Event;
	import flash.events.ProgressEvent;
	import flash.net.Socket;
	import flash.utils.ByteArray;
	import org.menacheri.jetclient.app.Session;
	import org.menacheri.jetclient.codecs.impl.LoginInOutCodecs;
	import org.menacheri.jetclient.codecs.InAndOutCodecChain;
	import org.menacheri.jetclient.codecs.Transform;
	import org.menacheri.jetclient.communication.MessageSender;
	import org.menacheri.jetclient.handlers.LoginHandler;
	import org.menacheri.jetclient.JetClient;
	import org.menacheri.jetclient.protocol.impl.MessageBufferProtocol;
	import org.menacheri.jetclient.protocol.Protocol;
	import org.menacheri.jetclient.util.LoginHelper;
	
	/**
	 * Class used to create a session in jetclient. SessionFactory will also create
	 * the actual connection to the jetserver by initializing a socket connection using 
	 * JetClient class.
	 * 
	 * @author Abraham Menacherry
	 * 
	 */
	public class SessionFactory 
	{
		/**
		 * This class holds a number of variables like username, password etc which
		 * are necessary for creating connections to remote jetserver.
		 */
		private var loginHelper:LoginHelper;
		private static var sessionId:uint = 0;
		
		public function SessionFactory(loginHelper:LoginHelper) 
		{
			this.loginHelper = loginHelper;
		}
		
		/**
		 * Creates and connects a session to the remote jetserver.
		 * 
		 * @return The session instance created.
		 *
		 */
		public function createAndConnectSession():Session 
		{
			var session:Session = createSession();
			connectSession(session, new MessageBufferProtocol(), new LoginInOutCodecs());
			return session;
		}
		
		/**
		 * @return Returns the session instance created using a
		 *         loginHelper.
		 */
		public function createSession():Session
		{
			sessionId++;
			var defaultSession:DefaultSession = new DefaultSession(sessionId);
			return defaultSession;
		}
		
		/**
		 * Connects the session to remote jetserver. Depending on the connection
		 * parameters provided to LoginHelper
		 */
		public function connectSession(session:Session,protocol:Protocol,loginCodecs:InAndOutCodecChain):void
		{
			if (null == protocol) {
				protocol = new MessageBufferProtocol();
			}
			protocol.applyProtocol(session);
			var jetClient:JetClient = new JetClient(loginHelper.getRemoteHost(), loginHelper.getRemotePort());
			var loginBytes:ByteArray = loginCodecs.getOutCodecs().transform(loginHelper.getLoginEvent()) as ByteArray;
			var socket:Socket = jetClient.connect(loginBytes);
			var messageSender:MessageSender = protocol.createMessageSender(socket);
			session.setMessageSender(messageSender);
			var loginHandler:LoginHandler = new LoginHandler(session, loginCodecs);
			socket.addEventListener(ProgressEvent.SOCKET_DATA, loginHandler.handleLogin);
		}
		
	}

}