package Board;

import java.awt.Image;
import java.awt.Toolkit;

public class Square {

	private Board playedBoard;
	private int i;
	private int j;
	private int value;
	private Image image;
	private int x;
	private int y;
	private boolean eaten;
	
	public Square(int x, int y, int value, String urlToImage, Board playedBoard, int i, int j)
	{
		this.playedBoard = playedBoard;
		this.x = x;
		this.y = y;
		this.i = i;
		this.j = j;
		this.value = value;
		this.image = Toolkit.getDefaultToolkit().getImage(urlToImage);
		this.eaten = false;
	}

	public Image getImage() 
	{
		return image;
	}
	public int getX() 
	{
		return x;
	}
	public int getY() 
	{
		return y;
	}
	public int getValue() 
	{
		return value;
	}
	public int getI() 
	{
		return i;
	}
	public int getJ() 
	{
		return j;
	}
	
	public void setI(int i) 
	{
		this.i = i;
	}
	public void setJ(int j) 
	{
		this.j = j;
	}

	public boolean isEaten() 
	{
		return eaten;
	}

	public void setImage(Image image) 
	{
		this.image = image;
	}
	public void setX(int x) 
	{
		this.x = x;
	}
	public void setY(int y) 
	{
		this.y = y;
	}
	public void setValue(int value) 
	{
		this.value = value;
	}
	public void setEaten(boolean eaten) 
	{
		this.eaten = eaten;
	}

	
	public void eat()
	{
		int scoreToAdd = 0;
		int currentScore = this.playedBoard.getScore();
		this.eaten = true;
		if (this.value == 0)
		{
			scoreToAdd = 10;
		}
		else
		{
			if (this.value == 1)
			{
				scoreToAdd = 50;
			}
			else
			{
				scoreToAdd = 100;
			}
		}
		if (((currentScore + scoreToAdd) / 10000) - (currentScore / 10000) == 1)
		{
			this.playedBoard.setAmountOfLives(this.playedBoard.getAmountOfLives() + 1);
		}
		this.playedBoard.setScore(this.playedBoard.getScore() + 10);
		this.image = Toolkit.getDefaultToolkit().getImage("Images/2.jpg");
		if (this.value == 1)
		{
			this.playedBoard.flipGhosts();
			new RunAwayThread(10, this.playedBoard).start();
		}
		else
		{
			if (this.value == 3)
			{
				this.playedBoard.throwBonus();
			}
		}
	}
	
	
	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof Square)
		{
			Square temp = (Square)obj;
			return (temp.x == this.x && temp.y == this.y);
		}
		return false;
	}
	
	@Override
	public String toString() 
	{
		return ("(" + this.i + ", " + this.j + ")");
	}
}
