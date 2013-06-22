package io.nadron.service.impl;

import io.nadron.service.UniqueIDGeneratorService;
import io.nadron.util.NadronConfig;

import java.util.concurrent.atomic.AtomicLong;


/**
 * Uses an atomic long to increment and provide a unique id. This will not work
 * in case of clustered servers.
 * 
 * @author Abraham.Menacherry
 * 
 */
public class SimpleUniqueIdGenerator implements UniqueIDGeneratorService 
{
	public static final AtomicLong ID = new AtomicLong(0l);
	
	@Override
	public Object generate() 
	{
		String nodeName = System.getProperty(NadronConfig.NODE_NAME);
		if (null == nodeName || "".equals(nodeName)) 
		{
			return ID.incrementAndGet();
		} 
		else 
		{
			return nodeName + ID.incrementAndGet();
		}
	}

	@Override
	public Object generateFor(@SuppressWarnings("rawtypes") Class klass) 
	{
		return klass.getSimpleName() + ID.incrementAndGet();
	}

}
