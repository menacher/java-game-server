package org.menacheri.zombie.game;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.menacheri.zombie.domain.ZombieCommands;


/**
 * This class acts like a message factory for creating game specific messages.
 * @author Abraham Menacherry
 *
 */
public class Messages
{
	public static ChannelBuffer apocalypse()
	{
		ChannelBuffer buffer = ChannelBuffers.buffer(4);
		int cmd = ZombieCommands.APOCALYPSE.getCommand();
		buffer.writeInt(cmd);
		return buffer;
	}
}
