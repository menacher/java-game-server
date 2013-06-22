package io.nadron.example.zombieclient;

import io.nadron.client.app.Session;
import io.nadron.client.app.impl.SessionFactory;
import io.nadron.client.communication.NettyMessageBuffer;
import io.nadron.client.communication.ReconnectPolicy;
import io.nadron.client.event.Event;
import io.nadron.client.event.impl.AbstractSessionEventHandler;
import io.nadron.client.util.LoginHelper;
import io.nadron.client.util.LoginHelper.LoginBuilder;
import io.nadron.example.zombie.domain.IAM;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ReconnectionTest
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		LoginBuilder builder = new LoginBuilder().username("user")
				.password("pass").connectionKey("Zombie_ROOM_1_REF_KEY_1")
				.nadronTcpHostName("localhost").tcpPort(18090)
				.nadronUdpHostName("255.255.255.255").udpPort(18090);
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
