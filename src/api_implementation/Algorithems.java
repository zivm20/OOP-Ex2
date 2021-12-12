package api_implementation;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import api.EdgeData;
import api.NodeData;

public class Algorithems implements DirectedWeightedGraphAlgorithms {
	/**
	 * 
	 * @author ziv morgan
	 * An element used in our floyd-warshall matrix
	 * @param path - LinkedList<NodeData> the path of the fastest route
	 * @param value - Double length of the path
	 *
	 */
	private class FloydElement{
		LinkedList<NodeData> path;
		Double value;
		FloydElement(LinkedList<NodeData> path,double value){
			this.path = path;
			this.value = value;
		}
		FloydElement(FloydElement e){
			this.path = new LinkedList<NodeData>(e.getPath());
			this.value = e.getValue();
		}
		FloydElement add(FloydElement other) {
			LinkedList<NodeData> newPath = new LinkedList<NodeData>(this.path);
			newPath.addAll(other.getPath());
			return new FloydElement(newPath, this.value + other.getValue());
		}
		LinkedList<NodeData> getPath(){
			return this.path;
		}
		Double getValue() {
			return this.value;
		}
		
	}
	DirectedWeightedGraph graph;
	HashMap<Integer,HashMap<Integer,FloydElement>> floydMatrix;
	public Algorithems(String json) {
		load(json);
	}
	public Algorithems(DirectedWeightedGraph g) {
		init(g);
	}
	
	@Override
	public void init(DirectedWeightedGraph g) {
		this.graph = g;
		
		this.floydMatrix = generateFloydWarshallMatrix();
		
	}

	@Override
	public DirectedWeightedGraph getGraph() {
		return this.graph;
	}

	@Override
	public DirectedWeightedGraph copy() {
		return new Graph(this.graph);
	}

	@Override
	public boolean isConnected() {
		//null graph is considered connected
		if(this.graph.nodeSize()==0) return true;
		
		//floydMatrix[src][dest] holds the length of the shortest route between node_src and node_dest or -1 if there is no such route
		for(int src = 0; src<this.graph.nodeSize(); src++) {
			for(int dest = 0; dest<this.graph.nodeSize(); dest++) {
				if(src != dest) {
					if(this.floydMatrix.get(src).get(dest).getValue()==-1) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	
	@Override
	public double shortestPathDist(int src, int dest) {
		
		return this.floydMatrix.get(src).get(dest).getValue();
	}
	@Override
	public List<NodeData> shortestPath(int src, int dest) {
		//RESTORE FROM FLOYED MATRIX WITHOUT USING A LINKED LIST
		return this.floydMatrix.get(src).get(dest).getPath();
	}
	/**
	 * Calculates the total weight of the path in lst
	 * @param lst - a list of nodes
	 * @return double
	 */
	private double pathLength(List<NodeData> lst) {
		double sum = -1;
		for(int i = 0; i<lst.size()-1; i++) {
			if(lst.get(i).getKey() != lst.get(i+1).getKey()) {
				EdgeData edge = this.graph.getEdge(lst.get(i).getKey(), lst.get(i+1).getKey());
				if(edge!=null) {
					sum += edge.getWeight();
				}
			}	
		}
		return sum;
	}
	/**
	 * generates a of size NxN where each element holds the distance of the fastest path between 2 nodes 
	 * and the path itself
	 * @return  ArrayList<ArrayList< FloydElement >>
	 */
	private  HashMap<Integer,HashMap<Integer,FloydElement>> generateFloydWarshallMatrix() {
		return generateFloydWarshallMatrix(new LinkedList<NodeData>());
	}
	
	/**
	 * generates a matrix of size NxN where each element holds the distance of the fastest path between 2 nodes
	 * that contains all nodes given in the list and the path itself
	 * @param req - List of nodes that must be in the fastest route
	 * @return ArrayList<ArrayList< FloydElement >>
	 */
	private HashMap<Integer,HashMap<Integer,FloydElement>> generateFloydWarshallMatrix( List<NodeData> req){
		
		//current matrix
		HashMap<Integer,HashMap< Integer,FloydElement >> matrix =
				new HashMap<Integer,HashMap< Integer,FloydElement >>();
		//matrix from the last iteration
		HashMap<Integer,HashMap< Integer,FloydElement >> lastMatrix = 
				new HashMap<Integer,HashMap< Integer,FloydElement >>();
		//we start with the base matrix
		for(Iterator<NodeData> iter1 = this.graph.nodeIter(); iter1.hasNext();) {
			int src = iter1.next().getKey();
			lastMatrix.put(src,new HashMap<Integer,FloydElement>() );
			for(Iterator<NodeData> iter2 = this.graph.nodeIter(); iter2.hasNext();) {
				int dest = iter2.next().getKey();
				EdgeData edge = this.graph.getEdge(src, dest);
				if(edge!=null) {
					LinkedList<NodeData> path = new LinkedList<NodeData>();
					path.add(this.graph.getNode(src));
					path.add(this.graph.getNode(dest));
					double dist = edge.getWeight();
					lastMatrix.get(src).put(dest,new FloydElement(path,dist));
				}
				else {
					lastMatrix.get(src).put(dest,new FloydElement(new LinkedList<NodeData>(),-1));
				}
			}
		}
		
		//for each vertex we update the matrix like so
		//if path_a_to_b is larger than path_a_to_vertex + path_vertex_to_b, we change path_a_to_b to path_a_to_vertex + path_vertex_to_b
		//unless the vertex is given as a requirement, in that case we will change path_a_to_b to path_a_to_vertex + path_vertex_to_b
		//regardless
		for(Iterator<NodeData> iter1 = this.graph.nodeIter(); iter1.hasNext();) {
			
			int matrixNum = iter1.next().getKey();
			matrix = new HashMap<Integer,HashMap<Integer,FloydElement>>();
			
			//find path_i_to_matrixNum and path_matrixNum_to_j for every vertex i and j on the graph
			for(Iterator<NodeData> iter2 = this.graph.nodeIter(); iter2.hasNext();) {
				int i = iter2.next().getKey();
				matrix.put(i, new HashMap<Integer,FloydElement>());
				matrix.get(i).put(matrixNum, lastMatrix.get(i).get(matrixNum));
			}
			matrix.put(matrixNum, new HashMap<Integer,FloydElement>(lastMatrix.get(matrixNum)));
			
			//update the matrix
			for(Iterator<NodeData> iter2 = this.graph.nodeIter(); iter2.hasNext();) {
				int src = iter2.next().getKey();
				if(src != matrixNum) {
					for(Iterator<NodeData> iter3 = this.graph.nodeIter(); iter3.hasNext();) {
						int dest = iter3.next().getKey();
						if(dest != matrixNum) {
							FloydElement half1 = matrix.get(src).get(matrixNum);
							FloydElement half2 = matrix.get(matrixNum).get(dest);
							//if a path doesn't exists between the 2 halves then keep the old value 
							if(half1.getValue() == -1 || half2.getValue() == -1) {
								matrix.get(src).put(dest, new FloydElement(lastMatrix.get(src).get(dest)));
							}
							//if a path does exist update the path iff this node is in the required list or this node makes the path shorter or 
							//a path didn't exist previously
							else if(req.contains(this.graph.getNode(matrixNum))  || 
									half1.add(half2).getValue() < lastMatrix.get(src).get(dest).getValue() ||
									lastMatrix.get(src).get(dest).getValue() == -1
									){
								
								matrix.get(src).put(dest, half1.add(half2));
							}
							//keep the old value if the old path is faster
							else {
								matrix.get(src).put(dest, new FloydElement(lastMatrix.get(src).get(dest)));
							}
						}
					}
				}
			}
			//update last matrix
			for(Iterator<NodeData> iter2 = this.graph.nodeIter(); iter2.hasNext();) {
				int i = iter2.next().getKey();
				lastMatrix.put(i, new HashMap<Integer,FloydElement>(matrix.get(i)));
			}
			System.out.println("done layer " + matrixNum);
			
		}
		return matrix;
	}
	
	
	
	@Override
	public NodeData center() {
		//return null if isn't connected graph
		if(!isConnected()) {
			return null;
		}
		
		ArrayList<Double> distances = new ArrayList<Double>(this.graph.nodeSize());
		//find for each node what is the maximum distance between itself and every other node
		for(int src = 0; src<this.graph.nodeSize(); src++) {
			distances.set(src,(double) -1);
			for(int dest = 0; dest<this.graph.nodeSize(); dest++) {
				distances.set( src, Math.max(distances.get(src),this.floydMatrix.get(src).get(dest).getValue()) );
			}
		}
		
		//the node with the minimum, maximum distance to all other nodes is our center
		//graph is connected so no need to check that distances[i] == -1
		int nodeIdx = 0;
		for(int i = 0; i<this.graph.nodeSize(); i++) {	
			if(distances.get(i) < distances.get(nodeIdx)) {
				nodeIdx = i;
			}
		}
		
		
		return this.graph.getNode(nodeIdx);
	}

	@Override
	public List<NodeData> tsp(List<NodeData> cities) {		
		//generateFloydWarshallMatrix(req) makes a floyd matrix such that each path contains the requirements
		HashMap<Integer,HashMap<Integer,FloydElement>> floydMatrix_requirements = generateFloydWarshallMatrix(cities);
		LinkedList<NodeData> bestPath = floydMatrix_requirements.get(cities.get(0).getKey()).get(cities.get(cities.size()-1).getKey()).getPath();
		//if the graph could not connect one of the nodes in cities to the path, return null
		for(NodeData node: cities) {
			if(!bestPath.contains(node)) {
				return null;
			}
		}
		return bestPath;
	}

	@Override
	public boolean save(String file) {
		String json = this.graph.toString();
		try {
			FileWriter f = new FileWriter(file);
			f.write(json);
			f.flush();
			f.close();
			return true;
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean load(String file) {
		try {
			DirectedWeightedGraph g = new Graph(file);
			init(g);
		}
		catch(Exception e) {
			return false;
		}
		
		return true;
	}

}
