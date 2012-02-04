package org.menacheri.server.netty;

import java.util.HashMap;
import java.util.Map;

import org.menacheri.context.AppContext;
import org.menacheri.server.IServerManager;


public class ServerManagerImpl implements IServerManager
{
	private Map<Integer,NettyServer> portAndServer;
	
	public ServerManagerImpl()
	{
		portAndServer = new HashMap<Integer, NettyServer>();
	}
	
	@Override
	public void startServers(int tcpPort, int flashPort, int udpPort)
	{
		if(tcpPort > 0)
		{
			NettyServer tcpServer = (NettyServer)AppContext.getBean(AppContext.TCP_SERVER);
			tcpServer.startServer(tcpPort);
			portAndServer.put(tcpPort, tcpServer);
		}
		
		if(flashPort > 0)
		{
			NettyServer flashServer = (NettyServer)AppContext.getBean(AppContext.FLASH_POLICY_SERVER);
			flashServer.startServer(flashPort);
			portAndServer.put(flashPort,flashServer);
		}
		
		if(udpPort > 0)
		{
			NettyServer udpServer = (NettyServer)AppContext.getBean(AppContext.UDP_SERVER);
			udpServer.startServer(udpPort);
			portAndServer.put(udpPort, udpServer);
		}
		
	}

	@Override
	public void stopServer(int[] ports)
	{
		if (null != ports && ports.length > 0){
			for (int i: ports){
				NettyServer server = portAndServer.get(ports[i]);
				server.stopServer();
			}
		}
	}

}
