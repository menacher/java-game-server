package org.menacheri.protocols;

public class UnknownProtocolException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4725237607479459847L;
	
	public UnknownProtocolException(String message)
	{
		super(message);
	}
	
	public UnknownProtocolException(String message, Exception e)
	{
		super(message,e);
	}
}
