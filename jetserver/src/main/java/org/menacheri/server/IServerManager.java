package org.menacheri.server;

/**
 * A generic interface used to manage a server.
 * @author Abraham Menacherry
 *
 */
public interface IServerManager
{
	public void startServers(int tcpPort, int flashPort, int udpPort);
	
	/**
	 * Used to stop the server and manage cleanup of resources. Server name is
	 * for future use, implementations may ignore this parameter.
	 * 
	 */
	public void stopServer(int[] ports);
}
