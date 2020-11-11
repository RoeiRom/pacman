package Players;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import Board.Board;
import Board.Square;
import Graph.Graph;
import Graph.Vertex;
import Graph.Edge;

public abstract class Ghost extends Thread implements Moveable, IAlgorithm {

	protected static final long SLEEPDURINGMOVE = 30;
	protected static final long SLEEPDURINGDEATH = 15;
	protected long sleep;
	protected int x;
	protected int y;
	protected Image[] movingGhost;
	protected static Image[] illGhost = null;
	protected static Image[] eyes = null;
	protected Direction ghostDirection;
	protected volatile GhostMode ghostMode;
	protected int picCount;
	protected Board playedBoard;
	protected volatile boolean threadFlag;
	protected Graph graph;
	
	public Ghost(String ghostType, Board playedBoard)
	{
		super("Ghost thread");
		this.graph = new Graph(playedBoard);
		if (illGhost == null)
		{
			illGhost = new Image[3];
			illGhost[0] = Toolkit.getDefaultToolkit().getImage("Images/Ill1.jpg");
			illGhost[1] = Toolkit.getDefaultToolkit().getImage("Images/Ill2.jpg");
			illGhost[2] = Toolkit.getDefaultToolkit().getImage("Images/IllWhite.jpg");
		}
		if (eyes == null)
		{
			eyes = new Image[4];
			for (int i = 0; i < eyes.length; i++) 
			{
				eyes[i] = Toolkit.getDefaultToolkit().getImage("Images/Eyes" + (i + 1) + ".jpg");
			}
		}
		this.movingGhost = new Image[8];
		for (int i = 0; i < 4; i++) 
		{
			for (int j = 0; j < 2; j++) 
			{
				this.movingGhost[2 * i + j] = Toolkit.getDefaultToolkit().getImage("Images/" + ghostType + (i + 1) + "_" + (j + 1) + ".jpg");
			}
		}
		
		this.playedBoard = playedBoard;
		this.picCount = 0;
		this.ghostMode = GhostMode.CHASING;
		this.ghostDirection = Direction.UP;
		this.sleep = SLEEPDURINGMOVE;
		this.threadFlag = true;
	}
	
	
	
	public void drawGhostOnBoard()
	{
		switch (this.ghostMode)
		{
		case CHASING:
			if (this.picCount++ % 2 == 0)
			{
				this.playedBoard.getOffGraphics().drawImage(this.movingGhost[2 * this.ghostDirection.getValue()], this.x - (Board.squareWidth / 2), this.y - (Board.squareWidth / 2), Board.squareWidth, Board.squareWidth, null);
			}
			else
			{
				this.playedBoard.getOffGraphics().drawImage(this.movingGhost[2 * this.ghostDirection.getValue() + 1], this.x - (Board.squareWidth / 2), this.y - (Board.squareWidth / 2), Board.squareWidth, Board.squareWidth, null);
			}
			break;
		case DEAD:
			this.playedBoard.getOffGraphics().drawImage(eyes[this.ghostDirection.getValue()], this.x - (Board.squareWidth / 2), this.y - (Board.squareWidth / 2), Board.squareWidth, Board.squareWidth, null);
			break;
		case ENDOFRUNNING:
			if (this.picCount++ % 2 == 0)
			{
				this.playedBoard.getOffGraphics().drawImage(illGhost[0], this.x - (Board.squareWidth / 2), this.y - (Board.squareWidth / 2), Board.squareWidth, Board.squareWidth, null);
			}
			else
			{
				this.playedBoard.getOffGraphics().drawImage(illGhost[2], this.x - (Board.squareWidth / 2), this.y - (Board.squareWidth / 2), Board.squareWidth, Board.squareWidth, null);
			}
			break;
		case RUNNING:
			if (this.picCount++ % 2 == 0)
			{
				this.playedBoard.getOffGraphics().drawImage(illGhost[0], this.x - (Board.squareWidth / 2), this.y - (Board.squareWidth / 2), Board.squareWidth, Board.squareWidth, null);
			}
			else
			{
				this.playedBoard.getOffGraphics().drawImage(illGhost[1], this.x - (Board.squareWidth / 2), this.y - (Board.squareWidth / 2), Board.squareWidth, Board.squareWidth, null);
			}
			break;
		}
	}

	@Override
	public void run() 
	{
		while (this.playedBoard.isLevelStarted() == false);
		this.getOutOfStartPlace();
		while (true)
		{
			if (this.threadFlag)
			{
				switch (this.ghostMode)
				{
				case CHASING:
					this.move();
					try 
					{
						Thread.sleep(50);
					}
					catch (InterruptedException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case RUNNING:
				case ENDOFRUNNING:
					this.runAway();
					try 
					{
						Thread.sleep(50);
					}
					catch (InterruptedException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case DEAD:
					die();
					break;
				}
			}
		}
	}
	
	@Override
	public void move() 
	{
		Square finalSquare;
		// TODO Auto-generated method stub
		LinkedList<Vertex> path = this.pathFromAlgorithm();
		Vertex vertex = path.getFirst();
		finalSquare = this.playedBoard.getSquareBoard()[vertex.getI()][vertex.getJ()];
		this.moveTowards(finalSquare);
	}

	abstract public void getOutOfStartPlace();
	abstract protected void getIntoStartPlace(Vertex startVertex);
	
	public void die()
	{
		if (Board.runAwayBonus <= 1600 && Board.runAwayBonus > 0)
		{
			int currentScore = this.playedBoard.getScore();
			int scoreToAdd = Board.runAwayBonus;
			if (((currentScore + scoreToAdd) / 10000) - (currentScore / 10000) == 1)
			{
				this.playedBoard.setAmountOfLives(this.playedBoard.getAmountOfLives() + 1);
			}
			this.playedBoard.setScore(currentScore + scoreToAdd);
			Board.runAwayBonus *= 2;
		}
		this.ghostMode = GhostMode.DEAD;
		this.sleep = SLEEPDURINGDEATH;
		Square next = null;
		Square start = this.playedBoard.getSquareInPlace(this.x, this.y);
		Vertex startVertex = new Vertex(start.getI(), start.getJ());
		Vertex endVertex = null;
		if (startVertex.getJ() > 13)
		{
			endVertex = new Vertex(11, 14);
		}
		else
		{
			endVertex = new Vertex(11, 13);
		}
		LinkedList<Vertex> path = this.graph.BFS(startVertex, endVertex);
		Iterator<Vertex> iter = path.iterator();
		while (iter.hasNext()) 
		{
			Vertex vertex = (Vertex) iter.next();
			next = this.playedBoard.getSquareBoard()[vertex.getI()][vertex.getJ()];
			this.moveTowards(next);
		}
		getIntoStartPlace(endVertex);
		this.ghostMode = GhostMode.CHASING;
		this.sleep = SLEEPDURINGMOVE;
		getOutOfStartPlace();
	}
	
	public void moveTowards(Square finalSquare)
	{
		Square initialSquare = this.playedBoard.getSquareInPlace(this.x, this.y);
		if (initialSquare.getI() == finalSquare.getI())
		{
			if (initialSquare.getI() == 14 && initialSquare.getJ() == 27 && finalSquare.getJ() == 0)
			{
				this.ghostDirection = Direction.RIGHT;
				this.x = this.playedBoard.getSquareBoard()[14][0].getX() + (Board.squareWidth / 2);
				this.y = this.playedBoard.getSquareBoard()[14][0].getY() + (Board.squareWidth / 2);
				return;
			}
			if (initialSquare.getI() == 14 && initialSquare.getJ() == 0 && finalSquare.getJ() == 27)
			{
				this.ghostDirection = Direction.LEFT;
				this.x = this.playedBoard.getSquareBoard()[14][27].getX() + (Board.squareWidth / 2);
				this.y = this.playedBoard.getSquareBoard()[14][27].getY() + (Board.squareWidth / 2);
				return;
			}
			//Moving Right
			if (initialSquare.getJ() < finalSquare.getJ())
			{
				int finalX = finalSquare.getX() + (Board.squareWidth / 2);
				this.ghostDirection = Direction.RIGHT;
				while (this.x < finalX)
				{
					this.x++;
					try 
					{
						Thread.sleep(this.sleep);
					} 
					catch (InterruptedException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			//Moving Left
			else
			{
				int finalX = finalSquare.getX() + (Board.squareWidth / 2);
				this.ghostDirection = Direction.LEFT;
				while (this.x > finalX)
				{
					this.x--;
					try 
					{
						Thread.sleep(this.sleep);
					} 
					catch (InterruptedException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		else
		{
			//Moving Down
			if (initialSquare.getI() < finalSquare.getI())
			{
				int finalY = finalSquare.getY() + (Board.squareWidth / 2);
				this.ghostDirection = Direction.DOWN;
				while (this.y < finalY)
				{
					this.y++;
					try 
					{
						Thread.sleep(this.sleep);
					} 
					catch (InterruptedException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			//Moving Up
			else
			{
				int finalY = finalSquare.getY() + (Board.squareWidth / 2);
				this.ghostDirection = Direction.UP;
				while (this.y > finalY)
				{
					this.y--;
					try 
					{
						Thread.sleep(this.sleep);
					} 
					catch (InterruptedException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	protected LinkedList<Vertex> generateNewPath(Vertex source)
	{
		Random r = new Random();
		Vector<Edge> nextAdjacents = this.graph.getAdjacents(source);
		LinkedList<Vertex> path = new LinkedList<Vertex>();
		Vertex next = null;
		switch (this.ghostDirection)
		{
		case DOWN:
			do
			{
				next = nextAdjacents.get(r.nextInt(nextAdjacents.size())).getAdjacent();
			}
			while (Vertex.verticesRelation(source, next) == Direction.UP);
			break;
		case LEFT:
			do
			{
				next = nextAdjacents.get(r.nextInt(nextAdjacents.size())).getAdjacent();
			}
			while (Vertex.verticesRelation(source, next) == Direction.RIGHT);
			break;
		case RIGHT:
			do
			{
				next = nextAdjacents.get(r.nextInt(nextAdjacents.size())).getAdjacent();
			}
			while (Vertex.verticesRelation(source, next) == Direction.LEFT);
			break;
		case UP:
			do
			{
				next = nextAdjacents.get(r.nextInt(nextAdjacents.size())).getAdjacent();
			}
			while (Vertex.verticesRelation(source, next) == Direction.DOWN);
			break;
		}
		path.add(next);
		return path;
	}
	
	public void runAway()
	{
		Pacman pacman = this.playedBoard.getPacman();
		Square ghostSquare = this.playedBoard.getSquareInPlace(this.x, this.y);
		Square pacmanSquare = this.playedBoard.getSquareInPlace(pacman.getX(), pacman.getY());
		Vertex ghostVertex = new Vertex(ghostSquare.getI(), ghostSquare.getJ());
		Vertex pacmanVertex = new Vertex(pacmanSquare.getI(), pacmanSquare.getJ());
		LinkedList<Vertex> path = this.graph.Dijkstra(ghostVertex, pacmanVertex);
		Direction relation = Vertex.verticesRelation(ghostVertex, path.getFirst());
		Vertex next = getOppositeVertex(ghostVertex, relation);
		Square nextSquare = this.playedBoard.getSquareBoard()[next.getI()][next.getJ()];
		this.moveTowards(nextSquare);
	}
	
	private Vertex getOppositeVertex(Vertex source, Direction relation)
	{
		Random r = new Random(System.currentTimeMillis());
		Vertex next;
		Vector<Edge> adjacents = this.graph.getAdjacents(source);
		switch (relation)
		{
		case DOWN:
			next = new Vertex(source.getI() - 1, source.getJ());
			if (adjacents.contains(new Edge(next)) == true)
			{
				return next;
			}
			else
			{
				next = adjacents.get(r.nextInt(adjacents.size())).getAdjacent();
				while (Vertex.verticesRelation(source, next) == Direction.DOWN)
				{
					next = adjacents.get(r.nextInt(adjacents.size())).getAdjacent();
				}
				return next;
			}
		case LEFT:
			next = new Vertex(source.getI(), source.getJ() + 1);
			if (adjacents.contains(new Edge(next)) == true)
			{
				return next;
			}
			else
			{
				next = adjacents.get(r.nextInt(adjacents.size())).getAdjacent();
				while (Vertex.verticesRelation(source, next) == Direction.LEFT)
				{
					next = adjacents.get(r.nextInt(adjacents.size())).getAdjacent();
				}
				return next;
			}
		case RIGHT:
			next = new Vertex(source.getI(), source.getJ() - 1);
			if (adjacents.contains(new Edge(next)) == true)
			{
				return next;
			}
			else
			{
				next = adjacents.get(r.nextInt(adjacents.size())).getAdjacent();
				while (Vertex.verticesRelation(source, next) == Direction.RIGHT)
				{
					next = adjacents.get(r.nextInt(adjacents.size())).getAdjacent();
				}
				return next;
			}
		case UP:
			next = new Vertex(source.getI() + 1, source.getJ());
			if (adjacents.contains(new Edge(next)) == true)
			{
				return next;
			}
			else
			{
				next = adjacents.get(r.nextInt(adjacents.size())).getAdjacent();
				while (Vertex.verticesRelation(source, next) == Direction.UP)
				{
					next = adjacents.get(r.nextInt(adjacents.size())).getAdjacent();
				}
				return next;
			}
		}
		return null;
	}

	public int getX() 
	{
		return x;
	}
	public int getY() 
	{
		return y;
	}
	public GhostMode getGhostMode()
	{
		return this.ghostMode;
	}
	public Direction getDirection()
	{
		return this.ghostDirection;
	}

	public void setX(int x) 
	{
		this.x = x;
	}
	public void setY(int y) 
	{
		this.y = y;
	}
	public void setGhostMode(GhostMode mode)
	{
		this.ghostMode = mode;
	}
	public void setThreadFlag(boolean flag)
	{
		this.threadFlag = flag;
	}
	public void SetDirection(Direction d)
	{
		this.ghostDirection = d;
	}
}
