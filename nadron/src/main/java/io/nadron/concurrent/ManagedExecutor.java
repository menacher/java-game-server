package io.nadron.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ManagedExecutor
{
	public static final List<ExecutorService> EXECUTOR_SERVICES = new ArrayList<ExecutorService>();

	static{
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run()
			{
				for(ExecutorService service: EXECUTOR_SERVICES){
					service.shutdown();
				}
			}
		});
	}
	
	public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory)
	{
		final ExecutorService exec = Executors.newSingleThreadExecutor(threadFactory);
		EXECUTOR_SERVICES.add(exec);
		return exec;
	}
}
