package io.nadron.app.impl;

import io.nadron.app.Game;
import io.nadron.app.GameCommandInterpreter;

/**
 * Domain object representing a game. This is a convenience implementation of
 * the {@link Game} interface so that simple games need not implement their own.
 * <b>Note</b> This implementation will throw exception if any of the setter
 * methods are invoked. All variables are final in this class and expected to be
 * set at object construction.
 * 
 * @author Abraham Menacherry
 * 
 */
public class SimpleGame implements Game {

	/**
	 * This variable could be used as a database key.
	 */
	private final Object id;

	/**
	 * The name of the game.
	 */
	private final String gameName;
	/**
	 * Each game has its own specific commands. This instance will be used to
	 * transform those commands(most probably in the form of bytes) to actual
	 * java method calls.
	 */
	private final GameCommandInterpreter gameCommandInterpreter;

	public SimpleGame(Object id, String gameName) {
		this(id, gameName, null);
	}

	public SimpleGame(Object id, String gameName,
			GameCommandInterpreter gameCommandInterpreter) {
		super();
		this.id = id;
		this.gameName = gameName;
		this.gameCommandInterpreter = gameCommandInterpreter;
	}

	/**
	 * Meant as a database access key.
	 * 
	 * @return The unique identifier of this Game.
	 */
	@Override
	public Object getId() {
		return id;
	}

	/**
	 * Meant as a database access key.
	 * 
	 * @param id
	 *            Set the unique identifier for this game.
	 */
	@Override
	public void setId(Object id) {
		throw new RuntimeException(new IllegalAccessException(
				"Game id is a final variable to be set at Game construction. "
						+ "It cannot be set again."));
	}

	@Override
	public String getGameName() {
		return gameName;
	}

	@Override
	public void setGameName(String gameName) {
		throw new RuntimeException(new IllegalAccessException(
				"GameName is a final variable to be set at Game construction. "
						+ "It cannot be set again."));
	}

	@Override
	public GameCommandInterpreter getGameCommandInterpreter() {
		return gameCommandInterpreter;
	}

	@Override
	public void setGameCommandInterpreter(GameCommandInterpreter interpreter) {
		throw new RuntimeException(new IllegalAccessException(
				"Game id is a final variable to be set at Game construction. "
						+ "It cannot be set again."));
	}

	@Override
	public synchronized Object unload() {
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((gameName == null) ? 0 : gameName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleGame other = (SimpleGame) obj;
		if (gameName == null) {
			if (other.gameName != null)
				return false;
		} else if (!gameName.equals(other.gameName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
