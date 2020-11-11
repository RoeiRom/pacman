package Players;

public enum Direction {
	UP(0),
	DOWN(1),
	LEFT(2),
	RIGHT(3);
	
	private int value;
	
	public int getValue()
	{
		return this.value;
	}
	
	private Direction(int value)
	{
		this.value = value;
	}
	
	public static Direction getOppisiteDirection(Direction d)
	{
		switch (d)
		{
		case DOWN:
			return UP;
		case LEFT:
			return RIGHT;
		case RIGHT:
			return LEFT;
		default:
			return DOWN;
		}
	}
	
	public static Direction convetFromInt(int value)
	{
		switch (value)
		{
		case 0:
			return UP;
		case 1:
			return DOWN;
		case 2:
			return LEFT;
		default:
			return RIGHT;
		}
	}
}
