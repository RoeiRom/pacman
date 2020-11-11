package Graph;

public class VertexWithDistance implements Comparable<VertexWithDistance> {

	private Vertex vertex;
	private int distance;
	
	public VertexWithDistance(Vertex vertex) 
	{
		this.vertex = vertex;
		this.distance = Integer.MAX_VALUE;
	}

	public Vertex getVertex()
	{
		return this.vertex;
	}
	public int getDistance()
	{
		return this.distance;
	}
	
	public void setDistance(int distance)
	{
		this.distance = distance;
	}
	
	@Override
	public int compareTo(VertexWithDistance o) 
	{
		return (this.distance - o.distance);
	}
}
