package org.menacheri.zombie.game;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.menacheri.zombie.domain.ZombieCommands;


/**
 * This class acts like a message factory for creating game specific messages.
 * @author Abraham Menacherry
 *
 */
public class Messages
{
	public static ByteBuf apocalypse()
	{
		ByteBuf buffer = Unpooled.buffer(4);
		int cmd = ZombieCommands.APOCALYPSE.getCommand();
		buffer.writeInt(cmd);
		return buffer;
	}
}
