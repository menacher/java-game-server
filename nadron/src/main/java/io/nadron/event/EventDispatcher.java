package io.nadron.event;

import io.nadron.app.Session;

import java.util.List;



/**
 * EventDispatcher's are associated with a session, so that the session can use
 * it to dispatch incoming events to the appropriate handlers.
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
	 * @param eventHandler The event handler to be added to the dispatcher.
	 */
    public void addHandler( EventHandler eventHandler );

	/**
	 * Returns the list of {@link EventHandler}s associated with a particular
	 * event type.
	 * 
	 * @param eventType
	 *            The type of event.
	 * @return The list {@link EventHandler}s associated with that event or
	 *         null.
	 */
	public List<EventHandler> getHandlers(int eventType);

	/**
	 * Removes an event handler from the dispatcher
	 * 
	 * @param eventHandler
	 *            the event handler to be removed from the dispatcher
	 */
	public void removeHandler(EventHandler eventHandler);

    /**
     * Removes all event listeners associated with the event type.
     */
    public void removeHandlersForEvent(int eventType);

	/**
	 * Removes all the handlers for a session.
	 * 
	 * @param session
	 *            The session instance from which event handlers need to be
	 *            removed.
	 * @return Returns true if all handlers were successfully removed.
	 */
	boolean removeHandlersForSession(Session session);
	
	/**
	 * Clears all handles associated with this dispatcher.
	 * 
	 */
	void clear();
    
    /**
     * Fires event in asynchronous mode
     *
     */
    public void fireEvent( Event event );

	/**
	 * Called by the session during disconnect, the dispatcher will no longer
	 * accept any events, it will also detach the existing listeners.
	 */
    public void close();

}
