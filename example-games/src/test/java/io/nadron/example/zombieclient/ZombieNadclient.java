package io.nadron.example.zombieclient;

import io.nadron.client.app.Session;
import io.nadron.client.app.impl.SessionFactory;
import io.nadron.client.communication.NettyMessageBuffer;
import io.nadron.client.event.Event;
import io.nadron.client.event.impl.AbstractSessionEventHandler;
import io.nadron.client.util.LoginHelper;
import io.nadron.client.util.LoginHelper.LoginBuilder;
import io.nadron.example.zombie.domain.IAM;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ZombieNadclient
{
	public static void main(String[] args) throws Exception
	{
		LoginBuilder builder = new LoginBuilder().username("user")
				.password("pass").connectionKey("Zombie_ROOM_1")
				.nadronTcpHostName("localhost").tcpPort(18090)
				.nadronUdpHostName("255.255.255.255").udpPort(18090);
		ScheduledExecutorService taskExecutor = Executors.newSingleThreadScheduledExecutor();
		SessionFactory sessionFactory = null;
		for(int i = 1; i<= 5 ; i++)
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
				taskExecutor.scheduleAtFixedRate(task, 2000, 200,
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
