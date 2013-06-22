package io.nadron.app;


/**
 * This interface has methods that need to be implemented by a game event. Normally
 * events would be generated for incoming message, connected, disconnected,
 * exception etc.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface GameEvent<T,O,E> {
	public T getPayload();
	public void setPayload(T payload);
	public PlayerSession getPlayerSession();
	public void setPlayerSession(PlayerSession playerSession);
	public O getOpCode();
	public void setOpcode(O opcode);
	public E getEventType();
	public void setEventType(E eventType);
	public String getEventName();
	public void setEventName(String eventName);
	public long getTimeStamp();
	public void setTimeStamp(long timeStamp);
}
