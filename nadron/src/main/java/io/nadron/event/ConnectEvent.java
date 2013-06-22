package io.nadron.event;

import io.nadron.communication.MessageSender.Fast;
import io.nadron.communication.MessageSender.Reliable;

public interface ConnectEvent extends Event
{
	public Reliable getTcpSender();
	public void setTcpSender(Reliable tcpSender);
	public Fast getUdpSender();
	public void setUdpSender(Fast udpSender);
}
