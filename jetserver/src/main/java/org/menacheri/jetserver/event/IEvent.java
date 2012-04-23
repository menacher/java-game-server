package org.menacheri.jetserver.event;

public interface IEvent
{
	int getType();

	void setType(int type);

	Object getSource();

	void setSource(Object source);

	IEventContext getEventContext();

	void setEventContext(IEventContext context);

	long getTimeStamp();

	void setTimeStamp(long timeStamp);
}
