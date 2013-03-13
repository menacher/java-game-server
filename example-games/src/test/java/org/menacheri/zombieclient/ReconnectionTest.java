package org.menacheri.zombieclient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.menacheri.jetclient.app.Session;
import org.menacheri.jetclient.app.impl.SessionFactory;
import org.menacheri.jetclient.communication.NettyMessageBuffer;
import org.menacheri.jetclient.communication.ReconnectPolicy;
import org.menacheri.jetclient.event.Event;
import org.menacheri.jetclient.event.impl.AbstractSessionEventHandler;
import org.menacheri.jetclient.util.LoginHelper;
import org.menacheri.jetclient.util.LoginHelper.LoginBuilder;
import org.menacheri.zombie.domain.IAM;

public class ReconnectionTest
{

	/**
	 * @param args
	 */
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
			Session session = sessionFactory.createAndConnectSession(getDefaultHandler());
			// Set the reconnect policy for reconnection.
			session.setReconnectPolicy(new ReconnectPolicy.ReconnectNTimes(2, 2000, loginHelper));
			GamePlay task = null;
			if((i % 2) == 0){
				task = new GamePlay(IAM.DEFENDER, session);
			}
			else{
				task = new GamePlay(IAM.ZOMBIE, session);
			}
			taskExecutor.scheduleAtFixedRate(task, 2000, 200, TimeUnit.MILLISECONDS);
		}
	}

	private static AbstractSessionEventHandler getDefaultHandler()
	{
		// we are only interested in data in, so override only that method.
		AbstractSessionEventHandler handler = new AbstractSessionEventHandler()
		{
			int i = 0;
			@Override
			public void onDataIn(Event event)
			{
				NettyMessageBuffer buffer = (NettyMessageBuffer)event.getSource();
				System.out.println("Remaining Human Population: " + buffer.readInt());
				i++;
				if(i == 3){
					throw new RuntimeException("Does Reconnect work?");
				}
			}
		};
		return handler;
	}
	
}
