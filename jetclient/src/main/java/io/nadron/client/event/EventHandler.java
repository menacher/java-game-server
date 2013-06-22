package io.nadron.client.event;

/**
 * A handler which can handle a specific event. Implementations of this class
 * get attaches to the {@link EventDispatcher}.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface EventHandler
{
	/**
	 * On event method which will be used to handle an incoming event dispatched
	 * to it by a {@link EventDispatcher}
	 * 
	 * @param event
	 */
	void onEvent(Event event);

	/**
	 * @return Returns the event type which is an integer. Using this event type
	 *         the event dispatcher can transmit matching events having the same
	 *         event type to this handler instance.
	 */
	int getEventType();

}
