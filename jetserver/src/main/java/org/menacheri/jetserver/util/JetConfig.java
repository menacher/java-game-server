package org.menacheri.jetserver.util;

public class JetConfig 
{
	public static final String NODE_NAME = "JetNode";
	public static final String RECONNECT_KEY = "RECONNECT_KEY";
	public static final String RECONNECT_REGISTRY = "RECONNECT_REGISTRY";
	/**
	 * By default wait for 5 minutes for remote client to reconnect, before
	 * closing session.
	 */
	public static final int DEFAULT_RECONNECT_DELAY =  5 * 60 * 1000;
}
