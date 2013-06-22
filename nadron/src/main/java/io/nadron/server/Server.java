package io.nadron.server;

import java.net.InetSocketAddress;

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
	
}
