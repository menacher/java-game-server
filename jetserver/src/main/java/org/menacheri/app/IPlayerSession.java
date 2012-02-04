package org.menacheri.app;

import org.jboss.netty.channel.group.ChannelGroup;
import org.jetlang.channels.MemoryChannel;
import org.menacheri.protocols.IProtocol;
import org.menacheri.service.IGameConnectionService;


/**
 * This interface abstracts a user session to a {@link IGame}. The
 * implementation class of this interface will not be used in isolation but
 * would be connected by the {@link IGameConnectionService} to a
 * {@link IGameRoom} based on some reference key or other input.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IPlayerSession extends ISession
{
	/**
	 * Each game room would have a "channel-group" to which the session has to
	 * subscribe. For e.g. it could be a <a
	 * href="http://code.google.com/p/jetlang/">jetlang</a>
	 * {@link MemoryChannel} or a <a
	 * href="http://www.jboss.org/netty/">Netty</a> {@link ChannelGroup} or some
	 * similar implementation which would basically group all the player
	 * sessions together.
	 * 
	 * @param nativeGameChannel
	 *            The MemoryChannel or ChannelGroup or similar collection
	 *            interface. TODO Pass this parameter in as an interface instead
	 *            of Object.
	 * @return Should return a handle to the subscription. Could be used for
	 *         unsbuscribing.
	 */
	public Object subscribeToGameChannel(Object nativeGameChannel);
	
	/**
	 * Each session is associated with a {@link IPlayer}. This is the actual
	 * human or machine using this session.
	 * 
	 * @return Returns that associated Player object or null if it is not
	 *         associated yet.
	 */
	public abstract IPlayer getPlayer();

	/**
	 * Set the {@link IPlayer} associated with this session. The lookup to find
	 * the player can be done using the lookup service based on some reference
	 * key provided by this session.
	 * 
	 * @param player
	 */
	public abstract void setPlayer(IPlayer player);

	/**
	 * Each user session is attached to a game room. This method is used to retrieve that
	 * game room object.
	 * 
	 * @return Returns the associated game room object or null if none is
	 *         associated.
	 */
	public abstract IGameRoom getGameRoom();

	/**
	 * Method used to set the game room for a particular session.
	 * 
	 * @param gameRoom
	 *            The gameRoom object to set.
	 */
	public abstract void setGameRoom(IGameRoom gameRoom);

	/**
	 * Get the {@link IProtocol} associated with this session.
	 * 
	 * @return Returns the associated enumeration or default AMF3_STRING.
	 */
	public IProtocol getProtocol();

	/**
	 * Set the protocol on the user session.
	 * 
	 * @param protocol
	 *            The {@link IProtocol} enumeration to set.
	 */
	public void setProtocol(IProtocol protocol);
}
