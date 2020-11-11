package Board;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Random;
import java.util.Vector;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Graph.Vertex;
import Players.Pacman;
import Players.Pinky;
import Players.Blinky;
import Players.Clyde;
import Players.Direction;
import Players.Ghost;
import Players.GhostMode;
import Players.Inky;

@SuppressWarnings("all")
public class Board extends JPanel implements Runnable {
	
	private MyFrame frame;
	private int score;
	private int level;
	private volatile boolean levelStarted;
	private int[][] board;
	private Square[][] squareBoard;
	private Image[][] phasingLevel;
	private Image[] bonuses;
	private int amountOfLives;
	private Image offScreen;
	private Graphics offGraphics;
	public static final int squareWidth = 20;
	public static int runAwayBonus = -1;
	
	private Pacman pacman;
	
	private Clyde clyde;
	private Pinky pinky;
	private Blinky blinky;
	private Inky inky;
	
	
	public Board(MyFrame m) 
	{
		super();
		this.levelStarted = false;
		this.frame = m;
		this.board = new int[31][28];
		this.squareBoard = new Square[31][28];
		this.phasingLevel = new Image[31][28];
		this.bonuses = new Image[7];
		for (int i = 0; i < bonuses.length; i++)
		{
			this.bonuses[i] = Toolkit.getDefaultToolkit().getImage("Images/Bonus" + (i + 1) + ".jpg");
		}
		this.fillBoard();
		this.fillSquareBoard();
		this.fillPhasingLevel();
		this.score = 0;
		this.level = 1;
		this.setSize(28 * squareWidth, 35 * squareWidth);
		this.setVisible(true);
		m.add(this);
		this.amountOfLives = 3;
		this.pacman = new Pacman(this);
		this.clyde = new Clyde(this);
		this.pinky = new Pinky(this);
		this.blinky = new Blinky(this);
		this.inky = new Inky(this);
		Thread t = new Thread(this);
		t.start();

	}
	
	private void fillPhasingLevel()
	{
		// TODO Auto-generated method stub
		for (int i = 0; i < 31; i++) 
		{
			for (int j = 0; j < 28; j++) 
			{
				if (board[i][j] < 0)
				{
					this.phasingLevel[i][j]  = Toolkit.getDefaultToolkit().getImage("Images/W" + ((-1) * this.board[i][j]) + ".jpg");
				}
				else
				{
					this.phasingLevel[i][j]  = Toolkit.getDefaultToolkit().getImage("Images/2.jpg");
				}
			}
		}
	}

	@Override
	public void run() 
	{
		this.offScreen = createImage(this.getWidth(), this.getHeight());
		this.offGraphics = offScreen.getGraphics();
		while (true)
		{
			startLevel();
			throwBonus();
			while (this.checkIfFinished() == false)
			{
				offGraphics.clearRect(0, 0, offScreen.getWidth(null), offScreen.getHeight(null));
				for (int i = 0; i < 31; i++)
				{
					for (int j = 0; j < 28; j++) 
					{
						offGraphics.drawImage(squareBoard[i][j].getImage(), squareBoard[i][j].getX(), squareBoard[i][j].getY(), squareWidth, squareWidth, null);
					}
				}
				Ghost collision = this.pacman.checkCollisionWithGhosts(this.getGhosts());
				if (collision == null || collision.getGhostMode() == GhostMode.DEAD)
				{
					this.clyde.drawGhostOnBoard();
					this.pinky.drawGhostOnBoard();
					this.blinky.drawGhostOnBoard();
					this.inky.drawGhostOnBoard();
					this.pacman.drawPacmanOnImage();
					this.drawDetails();
					try 
					{
						Thread.sleep(200);
					} 
					catch (InterruptedException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					finally
					{
						this.getGraphics().drawImage(offScreen, 0, 0, null);
					}
				}
				else
				{
					if (collision.getGhostMode() == GhostMode.CHASING)
					{
						this.setThreadFlags(false);
						this.pacman.die();
						if (this.amountOfLives == 0)
						{
							break;
						}
						this.setThreadFlags(true);
					}
					else
					{
						if (collision.getGhostMode() == GhostMode.RUNNING || collision.getGhostMode() == GhostMode.ENDOFRUNNING)
						{
							collision.setGhostMode(GhostMode.DEAD);
						}
					}
				}
				this.levelStarted = true;
			}
			if (this.amountOfLives == 0)
			{
				break;
			}
			nextLevel();
		}
		gameOver();
	}
	
	private void startLevel()
	{
		// TODO Auto-generated method stub
		this.pacman.start();
		this.clyde.start();
		this.pinky.start();
		this.blinky.start();
		this.inky.start();
		Image ready = Toolkit.getDefaultToolkit().getImage("Images/Ready.jpg");
		for (int t = 0; t < 30; t++)
		{
			offGraphics.clearRect(0, 0, offScreen.getWidth(null), offScreen.getHeight(null));
			for (int i = 0; i < 31; i++)
			{
				for (int j = 0; j < 28; j++) 
				{
					offGraphics.drawImage(squareBoard[i][j].getImage(), squareBoard[i][j].getX(), squareBoard[i][j].getY(), squareWidth, squareWidth, null);
				}
			}
			offGraphics.drawImage(ready, squareBoard[17][11].getX(), squareBoard[17][11].getY(), 6 * squareWidth, squareWidth, null);
			drawDetails();
			this.getGraphics().drawImage(offScreen, 0, 0, null);
			try
			{
				Thread.sleep(100);
			} 
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.levelStarted = true;
	}

	private void setThreadFlags(boolean flag)
	{
		Vector<Ghost> ghost = this.getGhosts();
		for (int i = 0; i < ghost.size(); i++) 
		{
			ghost.get(i).setThreadFlag(flag);
		}
		this.pacman.setThreadFlag(flag);
	}
	
	public void setGhostsModes(GhostMode modeToSet)
	{ 
		Vector<Ghost> ghost = this.getGhosts();
		for (int i = 0; i < ghost.size(); i++) 
		{
			if (ghost.get(i).getGhostMode() != GhostMode.DEAD)
			{
				ghost.get(i).setGhostMode(modeToSet);
			}
		}
	}
	
	private void fillBoard()
	{
		//Row 0
		this.board[0][0] = -6;
		for (int i = 1; i <= 12; i++)
		{
			this.board[0][i] = -1;
		}
		this.board[0][13] = -10;
		this.board[0][14] = -11;
		for (int i = 15; i <= 26; i++)
		{
			this.board[0][i] = -1;
		}
		this.board[0][27] = -7;
		
		//Row 1
		this.board[1][0] = -4;
		for (int i = 1; i <= 12; i++)
		{
			this.board[1][i] = 0;
		}
		this.board[1][13] = -12;
		this.board[1][14] = -13;
		for (int i = 15; i <= 26; i++)
		{
			this.board[1][i] = 0;
		}
		this.board[1][27] = -5;
		
		//Row 2
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
				this.board[2][i] = -4;
				this.board[2][j] = -5;
				break;
			case 1:
			case 6:
			case 12:
				this.board[2][i] = 0;
				this.board[2][j] = 0;
				break;
			case 2:
			case 7:
				this.board[2][i] = -16;
				this.board[2][j] = -17;
				break;
			case 3:
			case 4:
			case 8:
			case 9:
			case 10:
				this.board[2][i] = -14;
				this.board[2][j] = -14;
				break;
			case 5:
			case 11:
				this.board[2][i] = -17;
				this.board[2][j] = -16;
				break;
			case 13:
				this.board[2][i] = -12;
				this.board[2][j] = -13;
				break;
			}
		}
		
		//Row 3
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
				this.board[3][i] = -4;
				this.board[3][j] = -5;
				break;
			case 1:
				this.board[3][i] = 1;
				this.board[3][j] = 1;
				break;
			case 6:
			case 12:
				this.board[3][i] = 0;
				this.board[3][j] = 0;
				break;
			case 2:
			case 7:
				this.board[3][i] = -12;
				this.board[3][j] = -13;
				break;
			case 3:
			case 4:
			case 8:
			case 9:
			case 10:
				this.board[3][i] = -2;
				this.board[3][j] = -2;
				break;
			case 5:
			case 11:
				this.board[3][i] = -13;
				this.board[3][j] = -12;
				break;
			case 13:
				this.board[3][i] = -12;
				this.board[3][j] = -13;
				break;
			}
		}
		
		//Row 4
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch(i)
			{
			case 0:
				this.board[4][i] = -4;
				this.board[4][j] = -5;
				break;
			case 1:
			case 6:
			case 12:
				this.board[4][i] = 0;
				this.board[4][j] = 0;
				break;
			case 2:
			case 7:
			case 13:
				this.board[4][i] = -18;
				this.board[4][j] = -19;
				break;
			case 3:
			case 4:
			case 8:
			case 9:
			case 10:
				this.board[4][i] = -15;
				this.board[4][j] = -15;
				break;
			case 5:
			case 11:
				this.board[4][i] = -19;
				this.board[4][j] = -18;
				break;
			}
		}
		
		//Row 5
		this.board[5][0] = -4;
		this.board[5][27] = -5;
		for (int i = 1; i <= 26; i++) 
		{
			this.board[5][i] = 0;
		}
		
		//Row 6
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
				this.board[6][i] = -4;
				this.board[6][j] = -5;
				break;
			case 1:
			case 6:
			case 9:
				this.board[6][i] = 0;
				this.board[6][j] = 0;
				break;
			case 2:
			case 7:
			case 10:
				this.board[6][i] = -16;
				this.board[6][j] = -17;
				break;
			case 3:
			case 4:
			case 11:
			case 12:
			case 13:
				this.board[6][i] = -14;
				this.board[6][j] = -14;
				break;
			case 5:
			case 8:
				this.board[6][i] = -17;
				this.board[6][j] = -16;
				break;
			}
		}
		
		//Row 7
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
				this.board[7][i] = -4;
				this.board[7][j] = -5;
				break;
			case 1:
			case 6:
			case 9:
				this.board[7][i] = 0;
				this.board[7][j] = 0;
				break;
			case 2:
			case 10:
				this.board[7][i] = -18;
				this.board[7][j] = -19;
				break;
			case 3:
			case 4:
			case 11:
			case 12:
				this.board[7][i] = -15;
				this.board[7][j] = -15;
				break;
			case 5:
				this.board[7][i] = -19;
				this.board[7][j] = -18;
				break;
			case 7:
				this.board[7][i] = -12;
				this.board[7][j] = -13;
				break;
			case 8:
				this.board[7][i] = -13;
				this.board[7][j] = -12;
				break;
			case 13:
				this.board[7][i] = -23;
				this.board[7][j] = -22;
				break;	
			}
		}
		
		//Row 8
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
				this.board[8][i] = -4;
				this.board[8][j] = -5;
				break;
			case 7:
			case 13:
				this.board[8][i] = -12;
				this.board[8][j] = -13;
				break;
			case 8:
				this.board[8][i] = -13;
				this.board[8][j] = -12;
				break;
			default:
				this.board[8][i] = 0;
				this.board[8][j] = 0;
				break;
			}
		}
		
		//Row 9
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
				this.board[9][i] = -8;
				this.board[9][j] = -9;
				break;
			case 1:
			case 2:
			case 3:
			case 4:
				this.board[9][i] = -3;
				this.board[9][j] = -3;
				break;
			case 5:
				this.board[9][i] = -31;
				this.board[9][j] = -28;
				break;
			case 6:
				this.board[9][i] = 0;
				this.board[9][j] = 0;
				break;
			case 7:
			case 13:
				this.board[9][i] = -12;
				this.board[9][j] = -13;
				break;
			case 8:
				this.board[9][i] = -20;
				this.board[9][j] = -21;
				break;
			case 9:
			case 10:
				this.board[9][i] = -14;
				this.board[9][j] = -14;
				break;
			case 11:
				this.board[9][i] = -17;
				this.board[9][j] = -16;
				break;
			case 12:
				this.board[9][i] = 2;
				this.board[9][j] = 2;
				break;
			}
		}
		
		//Row 10
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				this.board[10][i] = -2;
				this.board[10][j] = -2;
				break;
			case 5:
				this.board[10][i] = -4;
				this.board[10][j] = -5;
				break;
			case 6:
				this.board[10][i] = 0;
				this.board[10][j] = 0;
				break;
			case 7:
				this.board[10][i] = -12;
				this.board[10][j] = -13;
				break;
			case 8:
				this.board[10][i] = -22;
				this.board[10][j] = -23;
				break;
			case 9:
			case 10:
				this.board[10][i] = -15;
				this.board[10][j] = -15;
				break;
			case 11:
				this.board[10][i] = -19;
				this.board[10][j] = -18;
				break;
			case 12:
				this.board[10][i] = 2;
				this.board[10][j] = 2;
				break;
			case 13:
				this.board[10][i] = -18;
				this.board[10][j] = -19;
				break;
			}
		}
		
		//Row 11
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				this.board[11][i] = -2;
				this.board[11][j] = -2;
				break;
			case 5:
				this.board[11][i] = -4;
				this.board[11][j] = -5;
				break;
			case 6:
				this.board[11][i] = 0;
				this.board[11][j] = 0;
				break;
			case 7:
				this.board[11][i] = -12;
				this.board[11][j] = -13;
				break;
			case 8:
				this.board[11][i] = -13;
				this.board[11][j] = -12;
				break;
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
				this.board[11][i] = 2;
				this.board[11][j] = 2;
				break;
			}
		}
		
		//Row 12
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				this.board[12][i] = -2;
				this.board[12][j] = -2;
				break;
			case 5:
				this.board[12][i] = -4;
				this.board[12][j] = -5;
				break;
			case 6:
				this.board[12][i] = 0;
				this.board[12][j] = 0;
				break;
			case 7:
				this.board[12][i] = -12;
				this.board[12][j] = -13;
				break;
			case 8:
				this.board[12][i] = -13;
				this.board[12][j] = -12;
				break;
			case 9:
				this.board[12][i] = 2;
				this.board[12][j] = 2;
				break;
			case 10:
				this.board[12][i] = -32;
				this.board[12][j] = -33;
				break;
			case 11:
				this.board[12][i] = -3;
				this.board[12][j] = -3;
				break;
			case 12:
				this.board[12][i] = -36;
				this.board[12][j] = -37;
				break;
			case 13:
				this.board[12][i] = -38;
				this.board[12][j] = -38;
				break;
			}
		}
		
		//Row 13
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				this.board[13][i] = -1;
				this.board[13][j] = -1;
				break;
			case 5:
				this.board[13][i] = -30;
				this.board[13][j] = -29;
				break;
			case 6:
				this.board[13][i] = 0;
				this.board[13][j] = 0;
				break;
			case 7:
				this.board[13][i] = -18;
				this.board[13][j] = -19;
				break;
			case 8:
				this.board[13][i] = -19;
				this.board[13][j] = -18;
				break;
			case 9:
				this.board[13][i] = 2;
				this.board[13][j] = 2;
				break;
			case 10:
				this.board[13][i] = -5;
				this.board[13][j] = -4;
				break;
			case 11:
			case 12:
			case 13:
				this.board[13][i] = -2;
				this.board[13][j] = -2;
				break;
			}
		}
		
		//Row 14
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 6:
				this.board[14][i] = 0;
				this.board[14][j] = 0;
				break;
			case 10:
				this.board[14][i] = -5;
				this.board[14][j] = -4;
				break;
			case 11:
			case 12:
			case 13:
				this.board[14][i] = -2;
				this.board[14][j] = -2;
				break;
			default:
				this.board[14][i] = 2;
				this.board[14][j] = 2;
				break;
			}
		}
		
		//Row 15
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				this.board[15][i] = -3;
				this.board[15][j] = -3;
				break;
			case 5:
				this.board[15][i] = -31;
				this.board[15][j] = -28;
				break;
			case 6:
				this.board[15][i] = 0;
				this.board[15][j] = 0;
				break;
			case 7:
				this.board[15][i] = -22;
				this.board[15][j] = -23;
				break;
			case 8:
				this.board[15][i] = -23;
				this.board[15][j] = -22;
				break;
			case 9:
				this.board[15][i] = 2;
				this.board[15][j] = 2;
				break;
			case 10:
				this.board[15][i] = -5;
				this.board[15][j] = -4;
				break;
			case 11:
			case 12:
			case 13:
				this.board[15][i] = -2;
				this.board[15][j] = -2;
				break;
			}
		}
		
		//Row 16
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				this.board[16][i] = -2;
				this.board[16][j] = -2;
				break;
			case 5:
				this.board[16][i] = -4;
				this.board[16][j] = -5;
				break;
			case 6:
				this.board[16][i] = 0;
				this.board[16][j] = 0;
				break;
			case 7:
				this.board[16][i] = -13;
				this.board[16][j] = -12;
				break;
			case 8:
				this.board[16][i] = -12;
				this.board[16][j] = -13;
				break;
			case 9:
				this.board[16][i] = 2;
				this.board[16][j] = 2;
				break;
			case 10:
				this.board[16][i] = -34;
				this.board[16][j] = -35;
				break;
			case 11:
			case 12:
			case 13:
				this.board[16][i] = -1;
				this.board[16][j] = -1;
				break;
			}
		}
		
		//Row 17
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				this.board[17][i] = -2;
				this.board[17][j] = -2;
				break;
			case 5:
				this.board[17][i] = -4;
				this.board[17][j] = -5;
				break;
			case 6:
				this.board[17][i] = 0;
				this.board[17][j] = 0;
				break;
			case 7:
				this.board[17][i] = -13;
				this.board[17][j] = -12;
				break;
			case 8:
				this.board[17][i] = -12;
				this.board[17][j] = -13;
				break;
			default:
				this.board[17][i] = 2;
				this.board[17][j] = 2;
				break;
			}
		}
		
		//Row 18
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				this.board[18][i] = -2;
				this.board[18][j] = -2;
				break;
			case 5:
				this.board[18][i] = -4;
				this.board[18][j] = -5;
				break;
			case 6:
				this.board[18][i] = 0;
				this.board[18][j] = 0;
				break;
			case 7:
				this.board[18][i] = -13;
				this.board[18][j] = -12;
				break;
			case 8:
				this.board[18][i] = -12;
				this.board[18][j] = -13;
				break;
			case 9:
				this.board[18][i] = 2;
				this.board[18][j] = 2;
				break;
			case 10:
				this.board[18][i] = -16;
				this.board[18][j] = -17;
				break;
			case 11:
			case 12:
			case 13:
				this.board[18][i] = -14;
				this.board[18][j] = -14;
				break;
			}
		}
		
		//Row 19
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
				this.board[19][i] = -6;
				this.board[19][j] = -7;
				break;
			case 1:
			case 2:
			case 3:
			case 4:
				this.board[19][i] = -1;
				this.board[19][j] = -1;
				break;
			case 5:
				this.board[19][i] = -30;
				this.board[19][j] = -29;
				break;
			case 6:
				this.board[19][i] = 0;
				this.board[19][j] = 0;
				break;
			case 7:
			case 10:
				this.board[19][i] = -18;
				this.board[19][j] = -19;
				break;
			case 8:
				this.board[19][i] = -19;
				this.board[19][j] = -18;
				break;
			case 9:
				this.board[19][i] = 2;
				this.board[19][j] = 2;
				break;
			case 11:
			case 12:
				this.board[19][i] = -15;
				this.board[19][j] = -15;
				break;
			case 13:
				this.board[19][i] = -23;
				this.board[19][j] = -22;
				break;
			}
		}
		
		//Row 20
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
				this.board[20][i] = -4;
				this.board[20][j] = -5;
				break;
			case 13:
				this.board[20][i] = -12;
				this.board[20][j] = -13;
				break;
			default:
				this.board[20][i] = 0;
				this.board[20][j] = 0;
				break;
			}
		}
		
		//Row 21
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
				this.board[21][i] = -4;
				this.board[21][j] = -5;
				break;
			case 1:
			case 6:
			case 12:
				this.board[21][i] = 0;
				this.board[21][j] = 0;
				break;
			case 2:
			case 7:
				this.board[21][i] = -16;
				this.board[21][j] = -17;
				break;
			case 3:
			case 4:
			case 8:
			case 9:
			case 10:
				this.board[21][i] = -14;
				this.board[21][j] = -14;
				break;
			case 5:
			case 11:
				this.board[21][i] = -17;
				this.board[21][j] = -16;
				break;
			case 13:
				this.board[21][i] = -12;
				this.board[21][j] = -13;
				break;
			}
		}
		
		//Row 22
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
				this.board[22][i] = -4;
				this.board[22][j] = -5;
				break;
			case 1:
			case 6:
			case 12:
				this.board[22][i] = 0;
				this.board[22][j] = 0;
				break;
			case 2:
			case 7:
			case 13:
				this.board[22][i] = -18;
				this.board[22][j] = -19;
				break;
			case 3:
			case 8:
			case 9:
			case 10:
				this.board[22][i] = -15;
				this.board[22][j] = -15;
				break;
			case 4:
				this.board[22][i] = -23;
				this.board[22][j] = -22;
				break;
			case 5:
				this.board[22][i] = -13;
				this.board[22][j] = -12;
				break;
			case 11:
				this.board[22][i] = -19;
				this.board[22][j] = -18;
				break;
			}
		}
		
		//Row 23
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
				this.board[23][i] = -4;
				this.board[23][j] = -5;
				break;
			case 1:
				this.board[23][i] = 1;
				this.board[23][j] = 1;
				break;
			case 4:
				this.board[23][i] = -12;
				this.board[23][j] = -13;
				break;
			case 5:
				this.board[23][i] = -13;
				this.board[23][j] = -12;
				break;
			case 13:
				this.board[23][i] = 2;
				this.board[23][j] = 2;
				break;
			default:
				this.board[23][i] = 0;
				this.board[23][j] = 0;
				break;
			}
		}
		
		//Row 24
		for (int i =0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
				this.board[24][i] = -24;
				this.board[24][j] = -26;
				break;
			case 1:
			case 11:
			case 12:
			case 13:
				this.board[24][i] = -14;
				this.board[24][j] = -14;
				break;
			case 2:
			case 8:
				this.board[24][i] = -17;
				this.board[24][j] = -16;
				break;
			case 3:
			case 6:
			case 9:
				this.board[24][i] = 0;
				this.board[24][j] = 0;
				break;
			case 4:
				this.board[24][i] = -12;
				this.board[24][j] = -13;
				break;
			case 5:
				this.board[24][i] = -13;
				this.board[24][j] = -12;
				break;
			case 7:
			case 10:
				this.board[24][i] = -16;
				this.board[24][j] = -17;
				break;
			}
		}
		
		//Row 25
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
				this.board[25][i] = -25;
				this.board[25][j] = -27;
				break;
			case 1:
			case 11:
			case 12:
				this.board[25][i] = -15;
				this.board[25][j] = -15;
				break;
			case 2:
			case 5:
				this.board[25][i] = -19;
				this.board[25][j] = -18;
				break;
			case 3:
			case 6:
			case 9:
				this.board[24][i] = 0;
				this.board[24][j] = 0;
				break;
			case 4:
			case 10:
				this.board[25][i] = -18;
				this.board[25][j] = -19;
				break;
			case 7:
				this.board[25][i] = -12;
				this.board[25][j] = -13;
				break;
			case 8:
				this.board[25][i] = -13;
				this.board[25][j] = -12;
				break;
			case 13:
				this.board[25][i] = -23;
				this.board[25][j] = -22;
				break;
			}
		}
		
		//Row 26
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
				this.board[26][i] = -4;
				this.board[26][j] = -5;
				break;
			case 7:
			case 13:
				this.board[26][i] = -12;
				this.board[26][j] = -13;
				break;
			case 8:
				this.board[26][i] = -13;
				this.board[26][j] = -12;
				break;
			default:
				this.board[26][i] = 0;
				this.board[26][j] = 0;
				break;
			}
		}
		
		//Row 27
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
				this.board[27][i] = -4;
				this.board[27][j] = -5;
				break;
			case 1:
			case 12:
				this.board[27][i] = 0;
				this.board[27][j] = 0;
				break;
			case 2:
				this.board[27][i] = -16;
				this.board[27][j] = -17;
				break;
			case 7:
				this.board[27][i] = -21;
				this.board[27][j] = -20;
				break;
			case 8:
				this.board[27][i] = -20;
				this.board[27][j] = -21;
				break;
			case 11:
				this.board[27][i] = -17;
				this.board[27][j] = -16;
				break;
			case 13:
				this.board[27][i] = -12;
				this.board[27][j] = -13;
				break;
			default:
				this.board[27][i] = -14;
				this.board[27][j] = -14;
				break;
			}
		}
		
		//Row 28
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
				this.board[28][i] = -4;
				this.board[28][j] = -5;
				break;
			case 1:
			case 12:
				this.board[28][i] = 0;
				this.board[28][j] = 0;
				break;
			case 2:
			case 13:
				this.board[28][i] = -18;
				this.board[28][j] = -19;
				break;
			case 11:
				this.board[28][i] = -19;
				this.board[28][j] = -18;
				break;
			default:
				this.board[28][i] = -15;
				this.board[28][j] = -15;
				break;	
			}
		}
		
		//Row 29
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
				this.board[29][i] = -4;
				this.board[29][j] = -5;
				break;
			default:
				this.board[29][i] = 0;
				this.board[29][j] = 0;
				break;
			}
		}
		
		//Row 30
		for (int i = 0, j = 27; i < j; i++, j--)
		{
			switch (i)
			{
			case 0:
				this.board[30][i] = -8;
				this.board[30][j] = -9;
				break;
			default:
				this.board[30][i] = -3;
				this.board[30][j] = -3;
				break;
			}
		}
	}
	
	private void fillSquareBoard()
	{
		for (int i = 0; i < 31; i++) 
		{
			for (int j = 0; j < 28; j++) 
			{
				if (board[i][j] < 0)
				{
					this.squareBoard[i][j]  = new Square(j * squareWidth, i * squareWidth, this.board[i][j], "Images/" + ((-1) * this.board[i][j]) + ".jpg", this, i, j);
				}
				else
				{
					switch (board[i][j])
					{
					case 0:
						this.squareBoard[i][j]  = new Square(j * squareWidth, i * squareWidth, this.board[i][j], "Images/Point.jpg", this, i, j);
						break;
					case 1:
						this.squareBoard[i][j]  = new Square(j * squareWidth, i * squareWidth, this.board[i][j], "Images/Energizer.jpg", this, i, j);
						break;
					case 2:
						this.squareBoard[i][j]  = new Square(j * squareWidth, i * squareWidth, this.board[i][j], "Images/2.jpg", this, i, j);
						this.squareBoard[i][j].setEaten(true);
						break;
					}
				}
			}
		}
		
		
	}

	public Square[][] getSquareBoard() 
	{
		return squareBoard;
	}

	public Graphics getOffGraphics() 
	{
		return offGraphics;
	}

	public Pacman getPacman() 
	{
		return pacman;
	}
	
	public int getScore()
	{
		return this.score;
	}
	
	public void setScore(int score)
	{
		this.score = score;
	}
	
	public Square getSquareInPlace(int x, int y)
	{
		try 
		{
			return this.squareBoard[y / Board.squareWidth][x / Board.squareWidth];
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			return null;
		}
	}
	
	public Vector<Ghost> getGhosts()
	{
		Vector<Ghost> ghosts = new Vector<Ghost>();
		ghosts.add(this.clyde);
		ghosts.add(this.pinky);
		ghosts.add(this.blinky);
		ghosts.add(this.inky);
		return ghosts;
	}
	
	public Image getOffScreen() 
	{
		return offScreen;
	}
	
	public int getAmountOfLives()
	{
		return this.amountOfLives;
	}
	
	public boolean isLevelStarted()
	{
		return levelStarted;
	}

	public void setAmountOfLives(int amountOfLives)
	{
		this.amountOfLives = amountOfLives;
	}

	private boolean checkIfFinished()
	{
		for (int i = 0; i < 31; i++) 
		{
			for (int j = 0; j < 28; j++) 
			{
				if ((this.squareBoard[i][j].getValue() == 0 || this.squareBoard[i][j].getValue() == 1) && this.squareBoard[i][j].isEaten() == false)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public void drawDetails()
	{
		Image img = Toolkit.getDefaultToolkit().getImage("Images/Closed.jpg");
		this.offGraphics.setColor(Color.BLACK);
		this.offGraphics.fillRect(squareBoard[30][0].getX(), squareBoard[30][0].getY() + Board.squareWidth, 28 * Board.squareWidth, 3 * Board.squareWidth);
		this.offGraphics.setColor(Color.WHITE);
		this.offGraphics.setFont(new Font("Comic Sans ms", Font.BOLD, Board.squareWidth));
		this.offGraphics.drawString("Score: " + this.score + "      Level: " + this.level, squareBoard[30][0].getX(), squareBoard[30][0].getY() + 2 * Board.squareWidth + (Board.squareWidth / 2));
		this.offGraphics.drawString("Lives: ", squareBoard[30][14].getX(), squareBoard[30][14].getY() + 2 * Board.squareWidth + (Board.squareWidth / 2));
		for (int i = 0; i < this.amountOfLives; i++) 
		{
			this.offGraphics.drawImage(img, squareBoard[30][17 + i].getX(), squareBoard[30][17 + i].getY() + Board.squareWidth + 3 * (Board.squareWidth / 4), Board.squareWidth, Board.squareWidth, null);
		}
	}
	
	private void nextLevel()
	{
		this.pacman.stop();
		this.clyde.stop();
		this.pinky.stop();
		this.blinky.stop();
		this.inky.stop();
		
		this.pacman = new Pacman(this);
		this.clyde = new Clyde(this);
		this.pinky = new Pinky(this);
		this.blinky = new Blinky(this);
		this.inky = new Inky(this);
		this.offScreen = createImage(this.getWidth(), this.getHeight());
		this.offGraphics = this.offScreen.getGraphics();
		for (int t = 0; t < 10; t++)
		{
			for (int i = 0; i < phasingLevel.length; i++)
			{
				for (int j = 0; j < phasingLevel[i].length; j++)
				{
					if (t % 2 == 0)
					{
						this.offGraphics.drawImage(this.phasingLevel[i][j], j * squareWidth, i * squareWidth, squareWidth, squareWidth, null);
					}
					else
					{
						this.offGraphics.drawImage(this.squareBoard[i][j].getImage(), j * squareWidth, i * squareWidth, squareWidth, squareWidth, null);
					}
				}
			}
			drawDetails();
			this.getGraphics().drawImage(offScreen, 0, 0, null);
			try
			{
				Thread.sleep(500);
			} 
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.fillSquareBoard();
		this.level++;
		this.levelStarted = false;
	}
	
	public void throwBonus()
	{
		int i, j;
		Random r = new Random(System.currentTimeMillis());
		do
		{
			i = r.nextInt(31);
			j = r.nextInt(28);
		} while (this.board[i][j] < 0);
		this.squareBoard[i][j].setValue(3);
		this.squareBoard[i][j].setImage(bonuses[r.nextInt(bonuses.length)]);
		this.squareBoard[i][j].setEaten(false);
	}
	
	public void flipGhosts()
	{
		Vector<Ghost> ghosts = this.getGhosts();
		for (int i = 0; i < ghosts.size(); i++)
		{
			ghosts.get(i).SetDirection(Direction.getOppisiteDirection(ghosts.get(i).getDirection()));
		}
	}
	
	private void gameOver()
	{
		this.pacman.stop();
		this.clyde.stop();
		this.pinky.stop();
		this.blinky.stop();
		this.inky.stop();
		Image empty = Toolkit.getDefaultToolkit().getImage("Images/2.jpg");
		Image gameOver = Toolkit.getDefaultToolkit().getImage("Images/GameOver.jpg");
		for (int k = 0; k < 2; k++)
		{
			offGraphics.clearRect(0, 0, this.offScreen.getHeight(null), this.offScreen.getHeight(null));
			for (int i = 0; i < 31; i++)
			{
				for (int j = 0; j < 28; j++) 
				{
					if (board[i][j] < 0)
					{
						offGraphics.drawImage(squareBoard[i][j].getImage(), squareBoard[i][j].getX(), squareBoard[i][j].getY(), squareWidth, squareWidth, null);
					}
					else
					{
						offGraphics.drawImage(empty, squareBoard[i][j].getX(), squareBoard[i][j].getY(), squareWidth, squareWidth, null);
					}
				}
			}
			offGraphics.drawImage(gameOver, squareBoard[17][10].getX(), squareBoard[17][10].getY(), 8 * Board.squareWidth, Board.squareWidth, null);
			this.drawDetails();
			this.getGraphics().drawImage(offScreen, 0, 0, null);
		}
		JOptionPane.showMessageDialog(null, "Game Over!");
		this.frame.dispose();
	}
}
