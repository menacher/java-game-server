package org.menacheri.event;


public interface IEventHandler
{
	/**
	 * On event
	 * 
	 * @param event
	 */
	public void onEvent(IEvent event);

	public int getEventType();

}
