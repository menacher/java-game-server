package io.nadron.app.impl 
{
	import flash.events.Event;
	import flash.events.ProgressEvent;
	import flash.net.Socket;
	import flash.utils.ByteArray;
	import io.nadron.app.Session;
	import io.nadron.codecs.impl.LoginInOutCodecs;
	import io.nadron.codecs.InAndOutCodecChain;
	import io.nadron.codecs.Transform;
	import io.nadron.communication.MessageSender;
	import io.nadron.handlers.LoginHandler;
	import io.nadron.NadClient;
	import io.nadron.protocol.impl.MessageBufferProtocol;
	import io.nadron.protocol.Protocol;
	import io.nadron.util.LoginHelper;
	
	/**
	 * Class used to create a session in jetclient. SessionFactory will also create
	 * the actual connection to the nadron server by initializing a socket connection using 
	 * NadClient class.
	 * 
	 * @author Abraham Menacherry
	 * 
	 */
	public class SessionFactory 
	{
		/**
		 * This class holds a number of variables like username, password etc which
		 * are necessary for creating connections to remote nadron server.
		 */
		private var loginHelper:LoginHelper;
		private static var sessionId:uint = 0;
		
		public function SessionFactory(loginHelper:LoginHelper) 
		{
			this.loginHelper = loginHelper;
		}
		
		/**
		 * Creates and connects a session to the remote nadron server.
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
			var nadClient:NadClient = new NadClient(loginHelper.getRemoteHost(), loginHelper.getRemotePort());
			var loginBytes:ByteArray = loginCodecs.getOutCodecs().transform(loginHelper.getLoginEvent()) as ByteArray;
			var socket:Socket = nadClient.connect(loginBytes);
			var messageSender:MessageSender = protocol.createMessageSender(socket);
			session.setMessageSender(messageSender);
			var loginHandler:LoginHandler = new LoginHandler(session, loginCodecs);
			socket.addEventListener(ProgressEvent.SOCKET_DATA, loginHandler.handleLogin);
		}
		
	}

}