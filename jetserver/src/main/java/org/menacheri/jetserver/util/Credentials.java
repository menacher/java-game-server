package org.menacheri.jetserver.util;

import org.jboss.netty.buffer.ChannelBuffer;

public class Credentials implements ICredentials
{
	private final String username;
	private final String password;
	
	public Credentials(ChannelBuffer buffer)
	{
		this.username = NettyUtils.readString(buffer);
		this.password = NettyUtils.readString(buffer);
	}

	/* (non-Javadoc)
	 * @see org.menacheri.jetserver.jetserver.util.ICredentials#getUsername()
	 */
	@Override
	public String getUsername()
	{
		return username;
	}

	/* (non-Javadoc)
	 * @see org.menacheri.jetserver.jetserver.util.ICredentials#getPassword()
	 */
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
