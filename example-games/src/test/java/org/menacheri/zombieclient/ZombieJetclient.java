package org.menacheri.zombieclient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.menacheri.jetclient.app.Session;
import org.menacheri.jetclient.app.impl.SessionFactory;
import org.menacheri.jetclient.communication.NettyMessageBuffer;
import org.menacheri.jetclient.event.Event;
import org.menacheri.jetclient.event.impl.AbstractSessionEventHandler;
import org.menacheri.jetclient.util.LoginHelper;
import org.menacheri.jetclient.util.LoginHelper.LoginBuilder;
import org.menacheri.zombie.domain.IAM;

public class ZombieJetclient
{
	public static void main(String[] args) throws Exception
	{
		LoginBuilder builder = new LoginBuilder().username("user")
				.password("pass").connectionKey("Zombie_ROOM_1")
				.jetserverTcpHostName("localhost").tcpPort(18090)
				.jetserverUdpHostName("255.255.255.255").udpPort(18090);
		ScheduledExecutorService taskExecutor = Executors.newSingleThreadScheduledExecutor();
		SessionFactory sessionFactory = null;
		for(int i = 1; i<=5 ; i++)
		{
			builder.connectionKey("Zombie_ROOM_" + i);
			LoginHelper loginHelper = builder.build();
			if (i == 1) 
			{
				sessionFactory = new SessionFactory(loginHelper);
			} 
			else 
			{
				// no need to create session factory objects again.
				sessionFactory.setLoginHelper(loginHelper);
			}

			for (int j = 1; j <= 20; j++) 
			{
				Session session = sessionFactory.createAndConnectSession();
				addDefaultHandlerToSession(session);
				GamePlay task = null;
				if ((i % 2) == 0) 
				{
					task = new GamePlay(IAM.DEFENDER, session);
				} else 
				{
					task = new GamePlay(IAM.ZOMBIE, session);
				}
				taskExecutor.scheduleAtFixedRate(task, 2000, 2000,
						TimeUnit.MILLISECONDS);
			}
		}
	}
	
	private static void addDefaultHandlerToSession(Session session)
	{
		// we are only interested in data in, so override only that method.
		AbstractSessionEventHandler handler = new AbstractSessionEventHandler(session)
		{
			@Override
			public void onDataIn(Event event)
			{
				NettyMessageBuffer buffer = (NettyMessageBuffer)event.getSource();
				System.out.println("Remaining Human Population: " + buffer.readInt());
			}
		};
		session.addHandler(handler);
	}
}
