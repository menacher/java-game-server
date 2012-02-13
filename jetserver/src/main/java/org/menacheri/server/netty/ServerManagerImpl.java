package org.menacheri.server.netty;

import java.util.HashSet;
import java.util.Set;

import org.menacheri.context.AppContext;
import org.menacheri.server.IServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerManagerImpl implements IServerManager
{
	private Set<NettyServer> servers;
	private static final Logger LOG = LoggerFactory.getLogger(ServerManagerImpl.class);
	
	public ServerManagerImpl()
	{
		servers = new HashSet<NettyServer>();
	}
	
	@Override
	public void startServers(int tcpPort, int flashPort, int udpPort) throws Exception
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
	public void startServers() throws Exception 
	{
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
	public void stopServers() throws Exception
	{
		for(NettyServer nettyServer: servers){
			try
			{
				nettyServer.stopServer();
			}
			catch (Exception e)
			{
				LOG.error("Unable to stop server {} due to error {}", nettyServer,e);
				throw e;
			}
		}
	}

}
