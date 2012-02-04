package org.menacheri.zombie.domain;

public enum ZombieCommands
{
	SHOT_GUN(1),EAT_BRAINS(2),SELECT_TEAM(3),APOCALYPSE(4);
	
	ZombieCommands(int cmd){
		
	}
	
	public static ZombieCommands getCommand(int command)
	{
		switch(command){
		case 1:
			return SHOT_GUN;
		case 2:
			return EAT_BRAINS;
		case 3:
			return SELECT_TEAM;
		case 4:
			return APOCALYPSE;
		default:
			return EAT_BRAINS;
		}
	}
	
	public static int getInt(ZombieCommands cmd)
	{
		switch (cmd)
		{
		case SHOT_GUN:
			return 1;
		case EAT_BRAINS:
			return 2;
		case SELECT_TEAM:
			return 3;
		case APOCALYPSE:
			return 4;
		default:
			return 1;
		}
	}
}
