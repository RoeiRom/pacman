package Graph;

public class Edge {

	private Vertex adjacent;
	private int weight;
	
	public Edge(Vertex adjacent)
	{
		this.adjacent = adjacent;
		this.weight = 1;
	}

	public Vertex getAdjacent() 
	{
		return adjacent;
	}
	public int getWeight() 
	{
		return weight;
	}

	public void setAdjacent(Vertex adjacent) 
	{
		this.adjacent = adjacent;
	}
	public void setWeight(int weight) 
	{
		this.weight = weight;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof Edge)
		{
			Edge temp = (Edge)obj;
			return (temp.adjacent.equals(this.adjacent));
		}
		return false;
	}
	
	@Override
	public String toString() 
	{
		return ("(" + this.adjacent.getI() + ", " + this.adjacent.getJ() + ")");
	}
}
