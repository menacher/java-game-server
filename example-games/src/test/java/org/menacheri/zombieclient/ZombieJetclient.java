package org.menacheri.zombieclient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.menacheri.jetclient.app.ISession;
import org.menacheri.jetclient.app.impl.SessionFactory;
import org.menacheri.jetclient.communication.NettyMessageBuffer;
import org.menacheri.jetclient.event.IEvent;
import org.menacheri.jetclient.event.impl.AbstractSessionEventHandler;
import org.menacheri.jetclient.util.LoginHelper;
import org.menacheri.jetclient.util.LoginHelper.LoginBuilder;
import org.menacheri.zombie.domain.IAM;

public class ZombieJetclient
{
	public static void main(String[] args) throws Exception
	{
		LoginBuilder builder = new LoginBuilder().username("user")
				.password("pass").connectionKey("Zombie_ROOM_1_REF_KEY_1")
				.jetserverTcpHostName("localhost").tcpPort(18090)
				.jetserverUdpHostName("255.255.255.255").udpPort(18090);
		LoginHelper loginHelper = builder.build();
		SessionFactory sessionFactory = new SessionFactory(loginHelper);
		ScheduledExecutorService taskExecutor = Executors.newSingleThreadScheduledExecutor();
		for(int i = 1; i<=50; i++){
			ISession session = getSession(sessionFactory);
			GamePlay task = null;
			if((i % 2) == 0){
				task = new GamePlay(IAM.DEFENDER, session);
			}
			else{
				task = new GamePlay(IAM.ZOMBIE, session);
			}
			taskExecutor.scheduleAtFixedRate(task, 5000, 100, TimeUnit.MILLISECONDS);
		}
	}
	
	private static ISession getSession(SessionFactory sessionFactory) throws Exception
	{
		ISession session = sessionFactory.createAndConnectSession();
		AbstractSessionEventHandler handler = new AbstractSessionEventHandler(session)
		{
			@Override
			public void onDataIn(IEvent event)
			{
				NettyMessageBuffer buffer = (NettyMessageBuffer)event.getSource();
				System.out.println("Remaining Human Population: " + buffer.readInt());
			}
		};
		session.addHandler(handler);
		return session;
	}
}
