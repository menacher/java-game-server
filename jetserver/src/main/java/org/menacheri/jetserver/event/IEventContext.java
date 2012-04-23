package org.menacheri.jetserver.event;

import org.menacheri.jetserver.app.ISession;

public interface IEventContext
{
	ISession getSession();
	void setSession(ISession session);
	
	/**
     * Retrieves an object which is {@link #setAttachment(Object) attached} to
     * this context.
     *
     * @return {@code null} if no object was attached or
     *                      {@code null} was attached
     */
    Object getAttachment();
    
    /**
     * Attaches an object to this context to store a stateful information
     * specific to the {@link IEvent} which is associated with this
     * context.
     */
    void setAttachment(Object attachement);
}
