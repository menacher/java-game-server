package org.menacheri.app;

import org.menacheri.app.impl.InvalidCommandException;

/**
 * This interface defines a command interpreter, which will basically map the
 * incoming bytes to a method call on some class.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IGameCommandInterpreter
{
	/**
	 * A generic method which can be used to interpret an incoming command from
	 * the client. The implementation would have a number of switch-case or
	 * if-else statements whereby command bytes can be transformed in actual
	 * java method calls.
	 * 
	 * @param command
	 *            Should mostly be of type {@link IGameEvent}, but flexibility
	 *            is provided to user to send in anything to the interpreter.
	 */
	public void interpretCommand(Object command) throws InvalidCommandException;
}
