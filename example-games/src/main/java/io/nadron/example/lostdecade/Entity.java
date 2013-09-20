package io.nadron.example.lostdecade;

import java.io.Serializable;

/**
 * A hero, monster or any other "living" character on the gameboard.
 * 
 * @author Abraham Menacherry
 * 
 */
public class Entity implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public static final String MONSTER = "MONSTER";
	public static final String HERO = "HERO";

	private String id;

	private int x, y, speed, key;

	private boolean press;

	/**
	 * Is it a monster or hero?
	 */
	private String type;

	/**
	 * Only heroes will have it.
	 */
	int score;

	public Entity()
	{

	}

	public Entity(String id, String type, int score)
	{
		super();
		this.id = id;
		this.type = type;
		this.score = score;
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public int getScore()
	{
		return score;
	}

	public void setScore(int score)
	{
		this.score = score;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String string)
	{
		this.id = string;
	}

	public int getSpeed()
	{
		return speed;
	}

	public void setSpeed(int speed)
	{
		this.speed = speed;
	}

	public int getKey()
	{
		return key;
	}

	public void setKey(int key)
	{
		this.key = key;
	}

	public boolean isPress()
	{
		return press;
	}

	public void setPress(boolean press)
	{
		this.press = press;
	}


}
