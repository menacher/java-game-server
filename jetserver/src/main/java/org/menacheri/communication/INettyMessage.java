package org.menacheri.communication;

import java.net.SocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

public interface INettyMessage extends IMessage
{
	ChannelBuffer getMessage();
	ChannelBuffer getChannelBuffer();
	INettyMessage setChannelBuffer(ChannelBuffer buffer);
	Channel getChannel();
	INettyMessage setChannel(Channel channel);
	SocketAddress getSocketAddress();
	INettyMessage setSocketAddress(SocketAddress remoteAddress);
}
