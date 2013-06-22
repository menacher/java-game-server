package io.nadron.event;

public interface Event
{
	int getType();

	void setType(int type);

	Object getSource();

	void setSource(Object source);

	EventContext getEventContext();

	void setEventContext(EventContext context);

	long getTimeStamp();

	void setTimeStamp(long timeStamp);
}
