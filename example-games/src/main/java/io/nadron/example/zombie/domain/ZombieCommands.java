package io.nadron.example.zombie.domain;

import java.util.HashMap;
import java.util.Map;

public enum ZombieCommands
{
	SHOT_GUN(1),EAT_BRAINS(2),SELECT_TEAM(3),APOCALYPSE(4),UNKNOWN(-1);
	final int command;
	
	ZombieCommands(int cmd){
		this.command = cmd;
	}
	
	public int getCommand()
	{
		return command;
	}
	
	public static class CommandsEnum
	{
		private static final Map<Integer, ZombieCommands> INT_COMMAND_MAP;
		static {
			INT_COMMAND_MAP = new HashMap<Integer, ZombieCommands>();
			for(ZombieCommands command: ZombieCommands.values()){
				INT_COMMAND_MAP.put(command.getCommand(), command);
			}
		}
		
		public static ZombieCommands fromInt(Integer i)
		{
			ZombieCommands command = INT_COMMAND_MAP.get(i);
			if(null == command){
				command = ZombieCommands.UNKNOWN;
			}
			return command;
		}
	}
}
