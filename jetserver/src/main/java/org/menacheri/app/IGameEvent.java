package org.menacheri.app;


/**
 * This interface has methods that need to be implemented by a game event. Normally
 * events would be generated for incoming message, connected, disconnected,
 * exception etc.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IGameEvent<T> {
	public T getPayload();
	public void setPayload(T payload);
	public IPlayerSession getPlayerSession();
	public void setPlayerSession(IPlayerSession playerSession);
	public int getOpCode();
	public void setOpCode(int opCode);
	public int getEventType();
	public void setEventType(int eventType);
	public String getEventName();
	public void setEventName(String eventName);
	public long getTimeStamp();
	public void setTimeStamp(long timeStamp);
}
