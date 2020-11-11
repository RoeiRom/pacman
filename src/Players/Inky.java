package Players;

import java.util.Iterator;
import java.util.LinkedList;

import Board.Board;
import Board.Square;
import Graph.Vertex;

public class Inky extends Ghost {
	
	public Inky(Board playedBoard) 
	{
		super("Inky", playedBoard);
		Square init = playedBoard.getSquareBoard()[14][12];
		this.x = init.getX() + (Board.squareWidth / 2);
		this.y = init.getY() + (Board.squareWidth / 2);
	}
	
	@Override
	public LinkedList<Vertex> pathFromAlgorithm() 
	{
		Square sourceSquare = this.playedBoard.getSquareInPlace(this.x, this.y);
		Pacman pacman = this.playedBoard.getPacman();
		Square destSquare = this.playedBoard.getSquareInPlace(pacman.getX(), pacman.getY());
		Vertex source = new Vertex(sourceSquare.getI(), sourceSquare.getJ());
		Vertex destination = new Vertex(destSquare.getI(), destSquare.getJ());
		LinkedList<Vertex> path = this.graph.DFS(source, destination);
		path.removeFirst();
		switch (this.ghostDirection)
		{
		case DOWN:
			if (Vertex.verticesRelation(source, path.getFirst()) == Direction.UP)
			{
				return generateNewPath(source);
			}
			break;
		case LEFT:
			if (Vertex.verticesRelation(source, path.getFirst()) == Direction.RIGHT)
			{
				return generateNewPath(source);
			}
			break;
		case RIGHT:
			if (Vertex.verticesRelation(source, path.getFirst()) == Direction.LEFT)
			{
				return generateNewPath(source);
			}
			break;
		case UP:
			if (Vertex.verticesRelation(source, path.getFirst()) == Direction.DOWN)
			{
				return generateNewPath(source);
			}
			break;
		}
		return path;
	}

	@Override
	public void getOutOfStartPlace() 
	{
		Square finalSquare;
		// TODO Auto-generated method stub
		LinkedList<Vertex> exitStart = new LinkedList<Vertex>();
		exitStart.add(new Vertex(14, 13));
		exitStart.add(new Vertex(13, 13));
		exitStart.add(new Vertex(12, 13));
		exitStart.add(new Vertex(11, 13));
		Iterator<Vertex> iter = exitStart.iterator();
		while (iter.hasNext()) 
		{
			Vertex vertex = (Vertex)iter.next();
			finalSquare = this.playedBoard.getSquareBoard()[vertex.getI()][vertex.getJ()];
			this.moveTowards(finalSquare);
			try 
			{
				Thread.sleep(50);
			} 
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.ghostDirection = Direction.UP;
	}
	
	@Override
	protected void getIntoStartPlace(Vertex startVertex) 
	{
		LinkedList<Vertex> enterStart = new LinkedList<Vertex>();
		// TODO Auto-generated method stub
		if (startVertex.getJ() == 13)
		{
			enterStart.add(new Vertex(12, 13));
			enterStart.add(new Vertex(13, 13));
			enterStart.add(new Vertex(14, 13));
			enterStart.add(new Vertex(14, 12));
		}
		else
		{
			enterStart.add(new Vertex(12, 14));
			enterStart.add(new Vertex(13, 14));
			enterStart.add(new Vertex(14, 14));
			enterStart.add(new Vertex(14, 13));
			enterStart.add(new Vertex(14, 12));
		}
		Iterator<Vertex> iter = enterStart.iterator();
		while (iter.hasNext()) 
		{
			Vertex vertex = (Vertex) iter.next();
			Square next = this.playedBoard.getSquareBoard()[vertex.getI()][vertex.getJ()];
			this.moveTowards(next);
		}
	}
}
