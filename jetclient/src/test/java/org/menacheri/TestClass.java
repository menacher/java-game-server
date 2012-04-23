package org.menacheri;

import java.net.UnknownHostException;

import org.menacheri.jetclient.app.Session;
import org.menacheri.jetclient.app.impl.SessionFactory;
import org.menacheri.jetclient.event.Event;
import org.menacheri.jetclient.event.impl.AbstractSessionEventHandler;
import org.menacheri.jetclient.util.LoginHelper;
import org.menacheri.jetclient.util.LoginHelper.LoginBuilder;

/**
 * A simple test class for connecting jetclient to a remote jetserver. This does
 * not have any game logic and will just print out events coming from the
 * server.
 * 
 * @author Abraham Menacherry
 * 
 */
public class TestClass
{

	/**
	 * @param args
	 * @throws Exception
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws UnknownHostException,
			Exception
	{
		LoginBuilder builder = new LoginBuilder().username("user")
				.password("pass").connectionKey("Zombie_ROOM_1_REF_KEY_1")
				.jetserverTcpHostName("localhost").tcpPort(18090)
				.jetserverUdpHostName("255.255.255.255").udpPort(18090);
		LoginHelper loginHelper = builder.build();
		SessionFactory sessionFactory = new SessionFactory(loginHelper);
		Session session = sessionFactory.createAndConnectSession();
		AbstractSessionEventHandler handler = new AbstractSessionEventHandler(
				session)
		{
			@Override
			public void onDataIn(Event event)
			{
				System.out.println("Received event: " + event);
			}
		};
		session.addHandler(handler);

	}

}
