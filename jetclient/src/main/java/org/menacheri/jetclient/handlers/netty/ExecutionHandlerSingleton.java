package org.menacheri.jetclient.handlers.netty;

import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;

/**
 * Creates an ExecutionHandler instance as a singleton.
 * @author Abraham Menacherry
 *
 */
public class ExecutionHandlerSingleton
{
	private static ExecutionHandler EXECUTION_HANDLER;

	public synchronized static ExecutionHandler getExecutionHandler()
	{
		if(null == EXECUTION_HANDLER){
			EXECUTION_HANDLER = new ExecutionHandler( new OrderedMemoryAwareThreadPoolExecutor(16, 1048576, 1048576));
		}
		return EXECUTION_HANDLER;
	}
	
}
