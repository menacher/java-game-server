package io.nadron.handlers;

import io.nadron.service.GameStateManagerService;

/**
 * Some handlers need to know the game state. This interface will be implemented
 * by such handlers so that there is a uniform way to get and set state on these
 * handlers.
 * 
 * @author Abraham Menacherry.
 * 
 */
public interface StateAware
{
	/**
	 * This method is used to get the state manager associated with this
	 * handler.
	 * 
	 * @return Returns the associated state manager instance, or null if none
	 *         are associated.
	 */
	public GameStateManagerService getGameStateManagerService();

	/**
	 * Method used to set the game state manager service on a state aware
	 * handler.
	 * 
	 * @param gameStateManagerService
	 *            The state manager instance to set.
	 */
	public void setGameStateManagerService(
			GameStateManagerService gameStateManagerService);

}
