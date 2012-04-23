package org.menacheri.jetclient.util;

import org.jboss.netty.buffer.ChannelBuffer;

public class SimpleCredentials implements ICredentials
{
	private final String username;
	private final String password;
	
	public SimpleCredentials(ChannelBuffer buffer)
	{
		this.username = NettyUtils.readString(buffer);
		this.password = NettyUtils.readString(buffer);
	}

	@Override
	public String getUsername()
	{
		return username;
	}

	@Override
	public String getPassword()
	{
		return password;
	}

	@Override
	public String toString()
	{
		return username;
	}
}
