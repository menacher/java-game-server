package org.menacheri;

import java.util.HashMap;
import java.util.Map;

import org.menacheri.app.IGameRoom;
import org.menacheri.service.ILookupService;
import org.menacheri.service.impl.LookupService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:/jetserver/beans/server-beans.xml")
public class JetServerSpringConfig
{

	public @Bean(name="lookupService") ILookupService lookupService()
	{
		Map<String,IGameRoom> refKeyGameRoomMap = new HashMap<String, IGameRoom>();
		LookupService service = new LookupService(refKeyGameRoomMap);
		return service;
	}
}
