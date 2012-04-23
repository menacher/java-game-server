package org.menacheri.jetserver.server;

import java.net.InetSocketAddress;

import org.menacheri.jetserver.app.ISession;

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
	
	ISession getSession();
	
	void setSession(ISession session);
}
