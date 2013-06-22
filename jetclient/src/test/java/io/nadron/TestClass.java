package io.nadron;

import io.nadron.client.app.Session;
import io.nadron.client.app.impl.SessionFactory;
import io.nadron.client.event.Event;
import io.nadron.client.event.impl.AbstractSessionEventHandler;
import io.nadron.client.util.LoginHelper;
import io.nadron.client.util.LoginHelper.LoginBuilder;

import java.net.UnknownHostException;


/**
 * A simple test class for connecting Nad client to a remote nadron server. This does
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
				.nadronTcpHostName("localhost").tcpPort(18090)
				.nadronUdpHostName("255.255.255.255").udpPort(18090);
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
