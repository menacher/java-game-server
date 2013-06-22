package io.nadron.app;

/**
 * Used to create sessions. Implementations of this factory can be passed on to
 * {@link GameRoom}'s which can then use it to create player sessions during
 * login.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface SessionFactory 
{

	public Session newSession();

	public PlayerSession newPlayerSession(GameRoom gameRoom, Player player);
}
