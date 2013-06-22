package io.nadron.event;

import org.jetlang.channels.ChannelSubscription;
import org.jetlang.core.Disposable;

/**
 * If the Event dispatcher uses Jetlang internally then it would require to
 * dispose of Jetlang {@link ChannelSubscription}s using the dispose method
 * during cleanup.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface JetlangDisposable
{
	public Disposable getDisposable();

	public void setDisposable(Disposable disposable);
}
