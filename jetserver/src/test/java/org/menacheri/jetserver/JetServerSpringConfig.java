package org.menacheri.jetserver;

import java.util.HashMap;
import java.util.Map;

import org.menacheri.jetserver.app.GameRoom;
import org.menacheri.jetserver.service.LookupService;
import org.menacheri.jetserver.service.impl.SimpleLookupService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:/jetserver/beans/server-beans.xml")
public class JetServerSpringConfig
{

	public @Bean(name="lookupService") LookupService lookupService()
	{
		Map<String,GameRoom> refKeyGameRoomMap = new HashMap<String, GameRoom>();
		SimpleLookupService service = new SimpleLookupService(refKeyGameRoomMap);
		return service;
	}
}
