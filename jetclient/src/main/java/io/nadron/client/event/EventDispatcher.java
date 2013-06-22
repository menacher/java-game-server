package io.nadron.client.event;

import io.nadron.client.app.Session;

import java.util.List;


/**
 * Event Dispatchers are used by {@link Session} to dispatch the incoming event
 * on its {@link Session#onEvent(Event)} method to the correct
 * {@link EventHandler}. This interface has methods for adding removing such
 * handlers.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface EventDispatcher
{
	/**
	 * Adds event handler to the dispatcher. Using this method, different events
	 * can be handled using different handlers.
	 * 
	 * @param eventHandler
	 *            The event handler to be added to the dispatcher.
	 */
	void addHandler(EventHandler eventHandler);

	/**
	 * Returns the list of {@link EventHandler}s associated with a particular
	 * event type.
	 * 
	 * @param eventType
	 *            The type of event.
	 * @return The list {@link EventHandler}s associated with that event or
	 *         null.
	 */
	List<EventHandler> getHandlers(int eventType);

	/**
	 * Removes an event handler from the dispatcher
	 * 
	 * @param eventHandler
	 *            the event handler to be removed from the dispatcher
	 */
	void removeHandler(EventHandler eventHandler);

	/**
	 * Removes all event listeners associated with the event type.
	 */
	void removeHandlersForEvent(int eventType);

	/**
	 * Removes all the handlers for a session.
	 * 
	 * @param session
	 * @return Returns true if all handlers were successfully removed.
	 */
	boolean removeHandlersForSession(Session session);

	/**
	 * Clears all handles associated with this dispatcher and returns the number
	 * of handlers cleared.
	 * 
	 */
	void clear();
	
	/**
	 * Fires event in asynchronous mode
	 * 
	 * @param event
	 */
	void fireEvent(Event event);

	/**
	 * Called by the session during disconnect, the dispatcher will no longer
	 * accept any events, it will also detach the existing listeners.
	 */
	void close();

}
