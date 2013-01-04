package org.menacheri.jetserver.server;

import java.net.InetSocketAddress;

import org.menacheri.jetserver.app.Session;

public interface Server {

	public interface TransmissionProtocol{
		
	}
	
	public enum TRANSMISSION_PROTOCOL implements TransmissionProtocol {
		TCP,UDP;
	}
	
	TransmissionProtocol getTransmissionProtocol();
	
	void startServer() throws Exception;
	
	void startServer(int port) throws Exception;;
	
	void startServer(InetSocketAddress socketAddress) throws Exception;
	
	void stopServer() throws Exception;
	
	InetSocketAddress getSocketAddress();
	
	Session getSession();
	
	void setSession(Session session);
}
