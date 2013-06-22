package io.nadron.app.impl;

public class InvalidCommandException extends Exception
{
	/**
	 * Eclipse generated serial id.
	 */
	private static final long serialVersionUID = 6458355917188516937L;
	
	public InvalidCommandException(String message)
	{
		super(message);
	}
	public InvalidCommandException(String message, Exception e)
	{
		super(message,e);
	}
}
