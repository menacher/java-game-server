package org.menacheri.server;

import java.net.InetAddress;

public interface IServer {

	public enum TRANSMISSION_PROTOCOL{
		TCP,UDP;
	}
	
	TRANSMISSION_PROTOCOL getTransmissionProtocol();
	
	void startServer() throws Exception;
	
	void startServer(InetAddress netAddress);
	
	void stopServer() throws Exception;
	
	InetAddress getAddress();
	
}
