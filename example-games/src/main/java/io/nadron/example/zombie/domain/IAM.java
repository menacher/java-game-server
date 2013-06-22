package io.nadron.example.zombie.domain;

public enum IAM
{
	ZOMBIE(1),DEFENDER(2);
	IAM(int who){
		
	}
	
	public static IAM getWho(int who)
	{
		switch (who)
		{
		case 1:
			return ZOMBIE;
		case 2:
			return DEFENDER;
		default:
			return ZOMBIE;
		}
	}
	
	public static int getInt(IAM iam)
	{
		switch (iam)
		{
		case ZOMBIE:
			return 1;
		case DEFENDER:
			return 2;
		default:
			return 1;
		}
	}
}
