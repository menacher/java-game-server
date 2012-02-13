package org.menacheri.server;

import java.net.InetSocketAddress;

public interface IServer {

	public enum TRANSMISSION_PROTOCOL{
		TCP,UDP;
	}
	
	TRANSMISSION_PROTOCOL getTransmissionProtocol();
	
	void startServer() throws Exception;
	
	void startServer(int port) throws Exception;;
	
	void startServer(InetSocketAddress socketAddress) throws Exception;
	
	void stopServer() throws Exception;
	
	InetSocketAddress getSocketAddress();
	
}
