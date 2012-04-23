package org.menacheri.jetserver.aspect;

import org.menacheri.jetserver.concurrent.Agent;

/**
 * This enumeration defines the type of concurrency that needs to be woven in by
 * <a href="www.eclipse.org/aspectj/">aspectj</a>. Agent is default, Actor is
 * not supported for now.
 * 
 * @author Abraham Menacherry
 * 
 */
public enum ConcurrencyType
{
	/**
	 * It is currently NOT supported.
	 */
	ACTOR,
	/**
	 * The annotated class would have <a
	 * href="http://code.google.com/p/jetlang/">Jetlang</a> {@link Agent}
	 * behavior "introduced" to it. This is the default.
	 */
	AGENT,
	/**
	 * No concurrency behavior will be woven in. User can use this to turn off
	 * concurrency on an AppManaged annotated class.
	 */
	NONE;
}
