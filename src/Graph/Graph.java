package Graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

import Board.Board;
import Board.Square;

public class Graph {

	private static Vertex[][] vertices = null;
	
	public Graph(Board board)
	{
		if (vertices == null)
		{
			vertices = new Vertex[31][28];
			Square[][] valuesMatrix = board.getSquareBoard();
			for (int i = 0; i < valuesMatrix.length; i++) 
			{
				for (int j = 0; j < valuesMatrix[i].length; j++) 
				{
					if (valuesMatrix[i][j].getValue() >= 0)
					{
						vertices[i][j] = new Vertex(i, j);
					}
					else
					{
						vertices[i][j] = null;
					}
				}
			}
			
			for (int i = 1; i < valuesMatrix.length - 1; i++) 
			{
				for (int j = 1; j < valuesMatrix[i].length - 1; j++) 
				{
					if (vertices[i][j] != null)
					{
						if (vertices[i - 1][j] != null)
						{
							vertices[i][j].addEdge(vertices[i - 1][j]);
						}
						if (vertices[i + 1][j] != null)
						{
							vertices[i][j].addEdge(vertices[i + 1][j]);
						}
						if (vertices[i][j - 1] != null)
						{
							vertices[i][j].addEdge(vertices[i][j - 1]);
						}
						if (vertices[i][j + 1] != null)
						{
							vertices[i][j].addEdge(vertices[i][j + 1]);
						}
					}
				}
			}
			vertices[14][0].addEdge(vertices[14][1]);
			vertices[14][0].addEdge(vertices[14][27]);
			vertices[14][27].addEdge(vertices[14][26]);
			vertices[14][27].addEdge(vertices[14][0]);
		}
	}
	
	public LinkedList<Vertex> BFS(Vertex source, Vertex destination)
	{
		Vertex sourceOfMatrix = vertices[source.getI()][source.getJ()];
		Vertex destinationOfMatrix = vertices[destination.getI()][destination.getJ()];
		Queue<Vertex> queue = new LinkedList<Vertex>();
		HashSet<Vertex> visited = new HashSet<Vertex>();
		//HashMap for parents of vertices, key is Vertex, value is Parent
		HashMap<Vertex, Vertex> parentalRelations = new HashMap<Vertex, Vertex>();
		parentalRelations.put(sourceOfMatrix, null);
		queue.add(sourceOfMatrix);
		while (queue.isEmpty() == false)
		{
			Vertex next = queue.poll();
			if (visited.contains(next) == false)
			{
				visited.add(next);
				for (Edge e : next.getAdjacents())
				{
					if (visited.contains(e.getAdjacent()) == false)
					{
						parentalRelations.put(e.getAdjacent(), next);
						queue.add(e.getAdjacent());
					}
				}
			}
		}
		LinkedList<Vertex> path = new LinkedList<Vertex>();
		path.add(destinationOfMatrix);
		while (parentalRelations.get(path.getLast()) != null)
		{
			path.add(parentalRelations.get(path.getLast()));
		}
		Collections.reverse(path);
		return path;
	}
	
	public LinkedList<Vertex> Dijkstra(Vertex source, Vertex destination)
	{
		VertexWithDistance[][] graphWithDistance = new VertexWithDistance[31][28];
		for (int i = 0; i < graphWithDistance.length; i++) 
		{
			for (int j = 0; j < graphWithDistance[i].length; j++) 
			{
				if (vertices[i][j] != null)
				{
					graphWithDistance[i][j] = new VertexWithDistance(vertices[i][j]);
				}
				else
				{
					graphWithDistance[i][j] = null;
				}
			}
		}
		
		VertexWithDistance sourceDistance = graphWithDistance[source.getI()][source.getJ()];
		VertexWithDistance destinationDistance = graphWithDistance[destination.getI()][destination.getJ()];
		//HashMap for parental Relations: key is vertex, value is parent
		HashMap<VertexWithDistance, VertexWithDistance> parentalRelations = new HashMap<VertexWithDistance, VertexWithDistance>();
		parentalRelations.put(sourceDistance, null);
		sourceDistance.setDistance(0);
		PriorityQueue<VertexWithDistance> priorityQueue = new PriorityQueue<VertexWithDistance>();
		priorityQueue.add(sourceDistance);
		while (priorityQueue.isEmpty() == false)
		{
			VertexWithDistance current = priorityQueue.poll();
			for (Edge e : current.getVertex().getAdjacents())
			{
				VertexWithDistance next = graphWithDistance[e.getAdjacent().getI()][e.getAdjacent().getJ()];
				int totalDistance = e.getWeight() + current.getDistance();
				if (totalDistance < next.getDistance())
				{
					next.setDistance(totalDistance);
					parentalRelations.put(next, current);
					priorityQueue.remove(next);
					priorityQueue.add(next);
				}
			}
		}
		
		LinkedList<Vertex> path = new LinkedList<Vertex>();
		path.add(destinationDistance.getVertex());
		while (parentalRelations.get(graphWithDistance[path.getLast().getI()][path.getLast().getJ()]) != null)
		{
			path.add(parentalRelations.get(graphWithDistance[path.getLast().getI()][path.getLast().getJ()]).getVertex());
		}
		Collections.reverse(path);
		return path;
	}
	
	public LinkedList<Vertex> DFS(Vertex source, Vertex destination)
	{
		//HashMap for parental relations, key is vertex value is parent
		HashMap<Vertex, Vertex> parentalRelations = new HashMap<Vertex, Vertex>();
		HashSet<Vertex> visited = new HashSet<Vertex>();
		Vertex sourceMatrix = vertices[source.getI()][source.getJ()];
		Vertex destinationMatrix = vertices[destination.getI()][destination.getJ()];
		//Initialization
		parentalRelations.put(sourceMatrix, null);
		visited.add(sourceMatrix);
		visitDFS(sourceMatrix, parentalRelations, visited);
		
		LinkedList<Vertex> path = new LinkedList<Vertex>();
		path.add(destinationMatrix);
		while (parentalRelations.get(path.getLast()) != null)
		{
			path.add(parentalRelations.get(path.getLast()));
		}
		Collections.reverse(path);
		return path;
	}
	
	private void visitDFS(Vertex vertex, HashMap<Vertex, Vertex> parentalRelations, HashSet<Vertex> visited)
	{
		visited.add(vertex);
		for (Edge e : vertex.getAdjacents()) 
		{
			if (visited.contains(e.getAdjacent()) == false)
			{
				parentalRelations.put(e.getAdjacent(), vertex);
				visitDFS(e.getAdjacent(), parentalRelations, visited);
			}
		}
	}
	
	public Vector<Edge> getAdjacents(Vertex source)
	{
		return vertices[source.getI()][source.getJ()].getAdjacents();
	}
}
