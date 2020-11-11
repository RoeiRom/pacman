package Players;

import java.util.Iterator;
import java.util.LinkedList;

import Board.Board;
import Board.Square;
import Graph.Vertex;

public class Clyde extends Ghost {

	public Clyde(Board playedBoard)
	{
		super("Clyde", playedBoard);
		Square init = playedBoard.getSquareBoard()[14][11];
		this.x = init.getX() + (Board.squareWidth / 2);
		this.y = init.getY() + (Board.squareWidth / 2);
	}
	
	@Override
	public LinkedList<Vertex> pathFromAlgorithm() 
	{
		Square sourceSquare = this.playedBoard.getSquareInPlace(this.x, this.y);
		Vertex source = new Vertex(sourceSquare.getI(), sourceSquare.getJ());
		return this.generateNewPath(source);
	}

	@Override
	public void getOutOfStartPlace() 
	{
		Square finalSquare;
		// TODO Auto-generated method stub
		LinkedList<Vertex> exitStart = new LinkedList<Vertex>();
		exitStart.add(new Vertex(14, 12));
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
		// TODO Auto-generated method stub
		LinkedList<Vertex> enterStart = new LinkedList<Vertex>();
		if (startVertex.getJ() == 13)
		{
			enterStart.add(new Vertex(12, 13));
			enterStart.add(new Vertex(13, 13));
			enterStart.add(new Vertex(14, 13));
			enterStart.add(new Vertex(14, 12));
			enterStart.add(new Vertex(14, 11));
		}
		else
		{
			enterStart.add(new Vertex(12, 14));
			enterStart.add(new Vertex(13, 14));
			enterStart.add(new Vertex(14, 14));
			enterStart.add(new Vertex(14, 13));
			enterStart.add(new Vertex(14, 12));
			enterStart.add(new Vertex(14, 11));
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
