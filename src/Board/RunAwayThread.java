package Board;

import Players.GhostMode;

public class RunAwayThread extends Thread {
	
	private int time;
	private Board playedBoard;
	
	public RunAwayThread(int time, Board playedBoard) 
	{
		this.time = time;
		this.playedBoard = playedBoard;
		Board.runAwayBonus = 200;
	}
	
	@Override
	public void run() 
	{
		if (Board.runAwayBonus == -1)
		{
			Board.runAwayBonus = 200;
		}
		this.playedBoard.setGhostsModes(GhostMode.RUNNING);
		while (time-- != 0)
		{
			if (time == 2)
			{
				this.playedBoard.setGhostsModes(GhostMode.ENDOFRUNNING);
			}
			try
			{
				Thread.sleep(1000);
			} 
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (Board.runAwayBonus != -1);
		{
			Board.runAwayBonus = -1;
		}
		this.playedBoard.setGhostsModes(GhostMode.CHASING);
	}
	
}
