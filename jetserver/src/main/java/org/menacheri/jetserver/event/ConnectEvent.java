package org.menacheri.jetserver.event;

import org.menacheri.jetserver.communication.MessageSender.Fast;
import org.menacheri.jetserver.communication.MessageSender.Reliable;

public interface ConnectEvent extends Event
{
	public Reliable getTcpSender();
	public void setTcpSender(Reliable tcpSender);
	public Fast getUdpSender();
	public void setUdpSender(Fast udpSender);
}
