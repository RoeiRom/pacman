package Players;

import Board.*;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.util.Vector;

import java.awt.Graphics;

public class Pacman extends Thread implements Moveable {

	private int x;
	private int y;
	private int picCount;
	private Direction pacmanDirection;
	private Image[] openPac;
	private Image[] deadPac;
	private Board playedBoard;
	private volatile boolean threadFlag;
	
	public Pacman(Board board) 
	{
		this.playedBoard = board;
		Square s = this.playedBoard.getSquareBoard()[17][12];
		this.x = s.getX() + (Board.squareWidth / 2);
		this.y = s.getY() + (Board.squareWidth / 2);
		this.openPac = new Image[4];
		for (int i = 0; i < openPac.length; i++) 
		{
			this.openPac[i] = Toolkit.getDefaultToolkit().getImage("Images/Open" + (i + 1) + ".jpg");
		}
		this.deadPac = new Image[12];
		this.deadPac[0] = Toolkit.getDefaultToolkit().getImage("Images/Closed.jpg");
		for (int i = 1; i < this.deadPac.length; i++) 
		{
			this.deadPac[i] = Toolkit.getDefaultToolkit().getImage("Images/Dead" + (i - 1) + ".jpg");
		}
		this.picCount = 0;
		this.pacmanDirection = Direction.LEFT;
		this.threadFlag = true;
	}
	
	@Override
	public void move() 
	{
		// TODO Auto-generated method stub
		switch (this.pacmanDirection)
		{
		case LEFT:
			try
			{
				if (this.playedBoard.getSquareInPlace(x - (Board.squareWidth / 2) - 1, y).getValue() >= 0)
				{
					if (this.playedBoard.getSquareInPlace(x - (Board.squareWidth / 2) - 1, y).getX() + Board.squareWidth == this.x && this.playedBoard.getSquareInPlace(x - (Board.squareWidth / 2) - 1, y).isEaten() == false)
					{
						this.playedBoard.getSquareInPlace(x - (Board.squareWidth / 2) - 1, y).eat();
					}
					x--;
				}
			}
			catch (NullPointerException e)
			{
				this.x = this.playedBoard.getSquareBoard()[14][27].getX() + (Board.squareWidth / 2);
				this.y = this.playedBoard.getSquareBoard()[14][27].getY() + (Board.squareWidth / 2);
			}
			break;
		case RIGHT:
			try
			{
				if (this.playedBoard.getSquareInPlace(x + (Board.squareWidth / 2) + 1, y).getValue() >= 0)
				{
					if (this.playedBoard.getSquareInPlace(x + (Board.squareWidth / 2) + 1, y).getX() == this.x && this.playedBoard.getSquareInPlace(x + (Board.squareWidth / 2) + 1, y).isEaten() == false)
					{
						this.playedBoard.getSquareInPlace(x + (Board.squareWidth / 2) + 1, y).eat();
					}
					x++;
				}
			}
			catch (NullPointerException e)
			{
				this.x = this.playedBoard.getSquareBoard()[14][0].getX() + (Board.squareWidth / 2);
				this.y = this.playedBoard.getSquareBoard()[14][0].getY() + (Board.squareWidth / 2);
			}
			break;
		case DOWN:
			if (this.playedBoard.getSquareInPlace(x, y + (Board.squareWidth / 2) + 1).getValue() >= 0)
			{
				if (this.playedBoard.getSquareInPlace(x, y + (Board.squareWidth / 2) + 1).getY() == this.y && this.playedBoard.getSquareInPlace(x, y + (Board.squareWidth / 2) + 1).isEaten() == false)
				{
					this.playedBoard.getSquareInPlace(x, y + (Board.squareWidth / 2) + 1).eat();
				}
				y++;
			}
			break;
		case UP:
			if (this.playedBoard.getSquareInPlace(x, y - (Board.squareWidth / 2) - 1).getValue() >= 0)
			{
				if (this.playedBoard.getSquareInPlace(x, y - (Board.squareWidth / 2) - 1).getY() + Board.squareWidth >= this.y && this.playedBoard.getSquareInPlace(x, y - (Board.squareWidth / 2) - 1).isEaten() == false)
				{
					this.playedBoard.getSquareInPlace(x, y - (Board.squareWidth / 2) - 1).eat();
				}
				y--;
			}
			break;
		}
	}
	
	
	
	@Override
	public void run() 
	{
		while (this.playedBoard.isLevelStarted() == false);
		while (true)
		{
			if (this.threadFlag)
			{
				this.move();
				try 
				{
					Thread.sleep(15);
				}
				catch (InterruptedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	
	}
	
	public void drawPacmanOnImage()
	{
		if (this.playedBoard.getOffGraphics() != null)
		{
			Image closed = Toolkit.getDefaultToolkit().getImage("Images/Closed.jpg");
			if ((this.picCount++) % 2 == 0)
			{
				this.playedBoard.getOffGraphics().drawImage(closed, this.x - (Board.squareWidth / 2), this.y - (Board.squareWidth / 2), Board.squareWidth, Board.squareWidth, null);
			}
			else
			{
				this.playedBoard.getOffGraphics().drawImage(this.openPac[this.pacmanDirection.getValue()], this.x - (Board.squareWidth / 2), this.y - (Board.squareWidth / 2), Board.squareWidth, Board.squareWidth, null);
			}
		}
	}
	
	public Ghost checkCollisionWithGhosts(Vector<Ghost> ghosts)
	{
		Ghost ghost;
		for (int i = 0; i < ghosts.size(); i++) 
		{
			ghost = ghosts.get(i);
			if (this.collisionWithGhost(ghost))
			{
				return ghost;
			}
		}
		return null;
	}
	
	public void die()
	{
		for (int i = 0; i < deadPac.length; i++) 
		{
			this.playedBoard.getOffGraphics().clearRect(this.x - (Board.squareWidth / 2), this.y - (Board.squareWidth / 2), Board.squareWidth, Board.squareWidth);
			this.playedBoard.getOffGraphics().drawImage(deadPac[i], this.x - (Board.squareWidth / 2), this.y - (Board.squareWidth / 2), Board.squareWidth, Board.squareWidth, null);
			this.playedBoard.drawDetails();
			this.playedBoard.getGraphics().drawImage(this.playedBoard.getOffScreen(), 0, 0, null);
			try 
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		this.playedBoard.setAmountOfLives(this.playedBoard.getAmountOfLives() - 1);
		this.initializePacman();
	}
	
	private boolean collisionWithGhost(Ghost ghost)
	{
		return Math.abs(this.x - ghost.getX()) < Board.squareWidth && Math.abs(this.y - ghost.getY()) < Board.squareWidth;
	}
	
	public int getX() 
	{
		return x;
	}
	public int getY() 
	{
		return y;
	}
	public Direction getPacmanDirection() 
	{
		return pacmanDirection;
	}
	public Image[] getDeadPac()
	{
		return this.deadPac;
	}

	public void setX(int x) 
	{
		this.x = x;
	}
	public void setY(int y) 
	{
		this.y = y;
	}
	public void setPacmanDirection(Direction pacmanDirection) 
	{
		this.pacmanDirection = pacmanDirection;
	}
	public void setThreadFlag(boolean flag)
	{
		this.threadFlag = flag;
	}

	public void initializePacman()
	{
		Square s = this.playedBoard.getSquareBoard()[17][12];
		this.x = s.getX() + (Board.squareWidth / 2);
		this.y = s.getY() + (Board.squareWidth / 2);
		this.pacmanDirection = Direction.LEFT;
	}

}
