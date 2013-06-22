package io.nadron.client.event;

/**
 * An event which will be transmitted to a session, remote server or client.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface Event
{
	int getType();

	void setType(int type);

	Object getSource();

	void setSource(Object source);

	long getTimeStamp();

	void setTimeStamp(long timeStamp);
}
