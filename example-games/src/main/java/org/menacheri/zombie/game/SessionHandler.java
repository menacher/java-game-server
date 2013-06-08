package org.menacheri.zombie.game;

import java.util.List;

import org.menacheri.jetserver.app.GameCommandInterpreter;
import org.menacheri.jetserver.app.Session;
import org.menacheri.jetserver.app.impl.InvalidCommandException;
import org.menacheri.jetserver.communication.DeliveryGuaranty.DeliveryGuarantyOptions;
import org.menacheri.jetserver.communication.MessageBuffer;
import org.menacheri.jetserver.communication.NettyMessageBuffer;
import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.NetworkEvent;
import org.menacheri.jetserver.event.impl.DefaultSessionEventHandler;
import org.menacheri.zombie.domain.Defender;
import org.menacheri.zombie.domain.IAM;
import org.menacheri.zombie.domain.Zombie;
import org.menacheri.zombie.domain.ZombieCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("rawtypes")
public class SessionHandler extends DefaultSessionEventHandler implements GameCommandInterpreter
{
	private static final Logger LOG = LoggerFactory.getLogger(SessionHandler.class);
	volatile int cmdCount;
	
	private Defender defender;
	private Zombie zombie;
	private IAM iam;
	
	public SessionHandler(Session session,Defender defender, Zombie zombie, IAM iam)
	{
		super(session);
		this.defender = defender;
		this.zombie = zombie;
		this.iam = iam;
	}
	
	public void onDataIn(Event event)
	{
		try 
		{
			interpretCommand(event.getSource());
		} 
		catch (InvalidCommandException e) 
		{
			e.printStackTrace();
			LOG.error("{}",e);
		}
	}
	
	
	@Override
	public void interpretCommand(Object command) throws InvalidCommandException
	{
		cmdCount++;
		int type;
		int operation;
		boolean isDefaultProtocol = true;
		if(command instanceof MessageBuffer) {
			MessageBuffer buf = (MessageBuffer) command;
			type = buf.readInt();
			operation = buf.readInt();
		}else{
			// websocket
			isDefaultProtocol = false;
			List<Double> data = (List)command;
			
			type = data.get(0).intValue();
			operation = data.get(1).intValue();
		}
		IAM iam = IAM.getWho(type);
		ZombieCommands cmd = ZombieCommands.CommandsEnum.fromInt(operation);
		switch (iam)
		{
		case ZOMBIE:
			switch (cmd)
			{
			case EAT_BRAINS:
				//LOG.trace("Interpreted command EAT_BRAINS");
				zombie.eatBrains();
				break;
			case SELECT_TEAM:
				LOG.trace("Interpreted command ZOMBIE SELECT_TEAM");
				selectTeam(iam);
				break;
			}
			break;
		case DEFENDER:
			switch (cmd)
			{
			case SHOT_GUN:
				//LOG.trace("Interpreted command SHOT_GUN");
				defender.shotgun();
				break;
			case SELECT_TEAM:
				LOG.trace("Interpreted command DEFENDER SELECT_TEAM");
				selectTeam(iam);
				break;
			}
			break;
			default:
				LOG.error("Received invalid command {}",cmd);
				throw new InvalidCommandException("Received invalid command" + cmd);
		}
		
		if((cmdCount % 10000) == 0)
		{
			NettyMessageBuffer buffer = new NettyMessageBuffer();
			//System.out.println("Command No: " + cmdCount);
			buffer.writeInt(cmdCount);
//			Event tcpEvent = Events.dataOutTcpEvent(buffer);
//			getSession().onEvent(tcpEvent);
			NetworkEvent udpEvent = null;
			if(isDefaultProtocol){
				udpEvent = Events.networkEvent(buffer, DeliveryGuarantyOptions.FAST);
			}else{
				udpEvent = Events.networkEvent(cmdCount);// for websocket protocol.
			}
			getSession().onEvent(udpEvent);
		}
	}

	public void selectTeam(IAM iam)
	{
		this.iam = iam;
	}
	
	public Defender getDefender()
	{
		return defender;
	}

	public void setDefender(Defender defender)
	{
		this.defender = defender;
	}

	public Zombie getZombie()
	{
		return zombie;
	}

	public void setZombie(Zombie zombie)
	{
		this.zombie = zombie;
	}

	public IAM getIam()
	{
		return iam;
	}

	public void setIam(IAM iam)
	{
		this.iam = iam;
	}

}
