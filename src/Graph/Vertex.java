package Graph;

import java.util.Vector;

import Players.Direction;

public class Vertex {

	protected int i; 
	protected int j;
	protected Vector<Edge> adjacents;
	
	public Vertex(int i, int j)
	{
		this.i = i;
		this.j = j;
		this.adjacents = new Vector<Edge>();
	}
	public Vertex(Vertex vertex)
	{
		this(vertex.i, vertex.j);
	}

	public void addEdge(Vertex vertex)
	{
		this.adjacents.add(new Edge(vertex));
	}
	
	public int getI() 
	{
		return i;
	}
	public int getJ() 
	{
		return j;
	}
	public Vector<Edge> getAdjacents()
	{
		return this.adjacents;
	}

	public void setI(int i) 
	{
		this.i = i;
	}
	public void setJ(int j) 
	{
		this.j = j;
	}
	
	
	/**
	 * The function receives 2 adjacent vertices: source and destination, 
	 * @param source
	 * @param destination
	 * @return the Direction from the source to the destination
	 * @author Roei Rom
	 * @since 8/4/2019
	 */
	public static Direction verticesRelation(Vertex source, Vertex destination)
	{
		if (source.getI() == 14 && source.getJ() == 0 && destination.getI() == 14 && destination.getJ() == 27)
		{
			return Direction.LEFT;
		}
		if (source.getI() == 14 && source.getJ() == 27 && destination.getI() == 14 && destination.getJ() == 0)
		{
			return Direction.RIGHT;
		}
		if (source.getI() == destination.getI())
		{
			if (source.getJ() < destination.getJ())
			{
				return Direction.RIGHT;
			}
			else
			{
				return Direction.LEFT;
			}
		}
		else
		{
			if (source.getI() < destination.getI())
			{
				return Direction.DOWN;
			}
			else
			{
				return Direction.UP;
			}
		}
	}
	
	@Override
	public String toString() 
	{
		return ("(" + this.i + ", " + this.j + ")");
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof Vertex)
		{
			Vertex temp = (Vertex) obj;
			return (temp.i == this.i && temp.j == this.j);
		}
		return false;
	}
}
