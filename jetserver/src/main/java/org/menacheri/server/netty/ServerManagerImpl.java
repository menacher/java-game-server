package org.menacheri.server.netty;

import java.util.HashSet;
import java.util.Set;

import org.menacheri.context.AppContext;
import org.menacheri.server.IServerManager;


public class ServerManagerImpl implements IServerManager
{
	private Set<NettyServer> servers;
	
	public ServerManagerImpl()
	{
		servers = new HashSet<NettyServer>();
	}
	
	@Override
	public void startServers(int tcpPort, int flashPort, int udpPort)
	{
		if(tcpPort > 0)
		{
			NettyServer tcpServer = (NettyServer)AppContext.getBean(AppContext.TCP_SERVER);
			tcpServer.startServer(tcpPort);
			servers.add(tcpServer);
		}
		
		if(flashPort > 0)
		{
			NettyServer flashServer = (NettyServer)AppContext.getBean(AppContext.FLASH_POLICY_SERVER);
			flashServer.startServer(flashPort);
			servers.add(flashServer);
		}
		
		if(udpPort > 0)
		{
			NettyServer udpServer = (NettyServer)AppContext.getBean(AppContext.UDP_SERVER);
			udpServer.startServer(udpPort);
			servers.add(udpServer);
		}
		
	}

	@Override
	public void startServers() {
		NettyServer tcpServer = (NettyServer)AppContext.getBean(AppContext.TCP_SERVER);
		tcpServer.startServer();
		servers.add(tcpServer);
		NettyServer flashServer = (NettyServer)AppContext.getBean(AppContext.FLASH_POLICY_SERVER);
		flashServer.startServer();
		servers.add(flashServer);
		NettyServer udpServer = (NettyServer)AppContext.getBean(AppContext.UDP_SERVER);
		udpServer.startServer();
		servers.add(udpServer);
	}
	
	@Override
	public void stopServers()
	{
		for(NettyServer nettyServer: servers){
			nettyServer.stopServer();
		}
	}

}
