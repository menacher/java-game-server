package io.nadron.example.lostdecade;

import io.nadron.client.app.Session;
import io.nadron.client.app.impl.SessionFactory;
import io.nadron.client.event.Event;
import io.nadron.client.event.impl.StartEventHandler;
import io.nadron.client.protocol.impl.NettyObjectProtocol;
import io.nadron.client.util.LoginHelper;
import io.nadron.client.util.LoginHelper.LoginBuilder;

import java.net.UnknownHostException;

public class LDClient {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException, Exception {
		LoginBuilder builder = new LoginBuilder().username("user")
				.password("pass").connectionKey("LDGameRoomForNettyClient")
				.nadronTcpHostName("localhost").tcpPort(18090);
		LoginHelper loginHelper = builder.build();
		SessionFactory sessionFactory = new SessionFactory(loginHelper);
		sessionFactory.setLoginHelper(loginHelper);
		final Session session = sessionFactory.createSession();
		
		// add handler for start event, and continue rest of game logic from there.
		session.addHandler(new StartEventHandler(session) {
			
			@Override
			public void onEvent(Event event) {
				System.out.println("Received start event, going to change protocol");
				session.resetProtocol(NettyObjectProtocol.INSTANCE);
				// create LDState objects send it test it etc from from here.
			}
		});
		
		// Connect the session, so that the above start event will be sent by server.
		sessionFactory.connectSession(session);
	}

}
