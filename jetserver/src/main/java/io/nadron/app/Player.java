package io.nadron.app;

/**
 * A Player is a human or machine that is playing single or multiple games. This
 * interface aims to abstract the basic properties of such a domain object.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface Player
{

	/**
	 * A unique key representing a gamer. This could be an email id or something
	 * unique.
	 * 
	 * @return Returns the unique key for the gamer.
	 */
	public Object getId();

	/**
	 * A unique key representing a gamer. This could be an email id or something
	 * unique.
	 * 
	 */
	public void setId(Object uniqueKey);

	/**
	 * Method used to get the name of the gamer.
	 * 
	 * @return Returns the name or null if none is associated.
	 */
	public String getName();

	/**
	 * Method used to set the name of the gamer.
	 * 
	 * @param name
	 *            Set the string name, strings more than 100 characters long may
	 *            be rejected.
	 */
	public void setName(String name);

	/**
	 * Method used to get the email id of the gamer.
	 * 
	 * @return Returns the email id string, null if none is set.
	 */
	public String getEmailId();

	/**
	 * Method used to set the email id of the gamer.
	 * 
	 * @param emailId
	 *            Sets the email id string. strings more than 50 characters long
	 *            may be rejected.
	 */
	public void setEmailId(String emailId);

	/**
	 * Add a session to a player. This session signifies the players
	 * session to a game.
	 * 
	 * @param session
	 *            The session to add.
	 * @return true if add was successful, false if not.
	 */
	public boolean addSession(PlayerSession session);

	/**
	 * Remove the players session to a game.
	 * 
	 * @param session
	 *            The session to remove.
	 * @return true if remove is successful, false other wise.
	 */
	public boolean removeSession(PlayerSession session);

	/**
	 * When a player logs out, this method can be called. It can also be called
	 * by the remove session method when it finds that there are no more
	 * sessions for this player.
	 * 
	 * @param playerSession
	 *            The session which is to be logged out.
	 */
	public void logout(PlayerSession playerSession);

}