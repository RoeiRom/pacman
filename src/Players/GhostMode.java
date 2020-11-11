package Players;

public enum GhostMode {

	CHASING(0),
	RUNNING(1),
	ENDOFRUNNING(2),
	DEAD(3);
	
	private int value;
	
	public int getValue()
	{
		return this.value;
	}
	
	private GhostMode(int value)
	{
		this.value = value;
	}
}
