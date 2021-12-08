package api_implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import api.EdgeData;
import api.NodeData;

public class Algorithems implements DirectedWeightedGraphAlgorithms {
	/**
	 * data structure that allows us to hold each node with its parents, children and the edge data between them
	 * 
	 * @author ziv morgan
	 *	
	 */
	private class Tree{
		NodeData node;
		HashMap<Tree, EdgeData> parents;
		HashMap<Tree, EdgeData> children;
		
		Tree(NodeData node){
			this.node = node;
			
			this.parents = new HashMap<Tree, EdgeData>();
			this.children = new HashMap<Tree, EdgeData>();
		}
		NodeData get() {
			return this.node;
		}
		void addParent(Tree n, EdgeData e) {
			this.parents.put(n, e);
		}
		void addChild(Tree n, EdgeData e) {
			this.children.put(n, e);
		}
		HashMap<Tree, EdgeData> getChildren() {
			return this.children;
		}
		
	}
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
	ArrayList<Tree> nodes;
	ArrayList<ArrayList< FloydElement >> floydMatrix;
	
	@Override
	public void init(DirectedWeightedGraph g) {
		this.graph = g;
		this.nodes = new ArrayList<Tree>();
		
		for(Iterator<NodeData> n = g.nodeIter(); n.hasNext(); ) {
			this.nodes.add(new Tree(n.next()));
		}
		for(Iterator<EdgeData> e = g.edgeIter(); e.hasNext(); ) {
			this.nodes.get(e.next().getSrc()).addChild(this.nodes.get(e.next().getDest()),e.next());
			this.nodes.get(e.next().getDest()).addParent(this.nodes.get(e.next().getSrc()),e.next());
		}
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
		/*
		//null graph is considered connected
				if(this.nodes.isEmpty()) return true;
				
				//init a boolean n by n matrix such that all its values are false apart from the diagonal
				boolean[][] pathExists = new boolean[this.nodes.size()][this.nodes.size()];
				for(int i=0;i<pathExists.length;i++) {
					for(int j=0;j<pathExists[i].length;j++) {
						pathExists[i][j] = i==j;
					}
				}
				
				//handle the base case
				for(Tree node: this.nodes) {
					for(Tree child: node.getChildren().keySet()) {
						pathExists[node.get().getKey()][child.get().getKey()] = true;
					}
					for(Tree parent: node.getParents().keySet()) {
						pathExists[parent.get().getKey()][node.get().getKey()] = true;
					}
				}
				
				//find a path between the remaining nodes that we haven't found a path between yet, updating our matrix in the process
				for(int i=0;i<pathExists.length;i++) {
					for(int j=0;j<pathExists[i].length;j++) {
						if(!pathExists[i][j]) {
							LinkedList<NodeData> lst = new LinkedList<NodeData>();
							pathExists[i][j] = findPath(i,j,lst,pathExists);
						}
					}
				}
				
				//if there still exists a pair without a route, return false else return true
				for(int i=0;i<pathExists.length;i++) {
					for(int j=0;j<pathExists[i].length;j++) {
						if(!pathExists[i][j]) {
							return false;
						}
					}
				}	
				return true;
				*/
		
		
		
		//null graph is considered connected
		if(this.nodes.isEmpty()) return true;
		
		//floydMatrix[src][dest] holds the length of the shortest route between node_src and node_dest or -1 if there is no such route
		for(int src = 0; src<this.nodes.size(); src++) {
			for(int dest = 0; dest<this.nodes.size(); dest++) {
				if(src != dest) {
					if(this.floydMatrix.get(src).get(dest).getValue()==-1) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Recursive function to find if a path exists between nodes src and dest 
	 * and updates an NxN matrix accordingly 
	 * @param src - starting node/current node
	 * @param dest - end node
	 * @param path - current path
	 * @param pathExists - NxN boolean matrix representing if a path exists from node a to node b
	 * @return - true iff there exists a path from src to dest
	 */
	private boolean findPath(int src, int dest, List<NodeData> path, boolean[][] pathExists) {
		//stopping conditions
		if(src == dest || pathExists[src][dest]) {
			return true;
		}
		else if(path.contains(this.nodes.get(src).get()) || this.nodes.get(src).getChildren().isEmpty() ){
			return false;
		}
		//make sure not to keep every single child node along the way, only the ones relevant to the path
		LinkedList<NodeData> temp =  new LinkedList<NodeData>(path);
		temp.add(this.nodes.get(src).get());
		
		for(Tree child: this.nodes.get(src).getChildren().keySet()) {
			//if (node a -> node b) exists and (node b -> node c) exists then there exists a path (node a -> node c)
 			for(NodeData node: temp) {
				pathExists[node.getKey()][child.get().getKey()] = true;
			}
			if(findPath(child.get().getKey(), dest, path,pathExists)) {
				return true;
			}
		}
		
		
		
		return false;
	}
	
	
	
	@Override
	public double shortestPathDist(int src, int dest) {
		
		return pathLength(shortestPath(src,dest));
	}

	@Override
	public List<NodeData> shortestPath(int src, int dest) {
		return this.floydMatrix.get(src).get(dest).getPath();
		/*
		List<NodeData> lst = new LinkedList<NodeData>();
		List<NodeData> bestList = new LinkedList<NodeData>();
		return shortestPath(src,dest, lst, bestList);
		*/
	}
	/**
	 * Recursive function to find the shortest path between src and dest
	 * @param src - src/current node
	 * @param dest - destination
	 * @param lst - current path
	 * @param bestList - shortest path found until now
	 * @return - if a path was found will return bestList else will return null
	 */
	private List<NodeData> shortestPath(int src, int dest, List<NodeData> lst, List<NodeData> bestList) {
		if(src == dest) {
			return lst;
		}
		else if(lst.contains(this.nodes.get(src).get()) || this.nodes.get(src).getChildren().isEmpty() ){
			return null;
		}
		
		lst.add(this.nodes.get(src).get());
		for(Map.Entry<Tree, EdgeData> entry: this.nodes.get(src).getChildren().entrySet()) {
			List<NodeData> temp = shortestPath(entry.getKey().get().getKey(),dest, lst, bestList);
			
			if(temp != null && pathLength(temp) < pathLength(bestList)) {
				bestList = temp;
			}
			
			lst.remove(entry.getKey().get());
		}
		return bestList;
		
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
				sum += this.nodes.get( lst.get(i).getKey()  ).getChildren().get( this.nodes.get(lst.get(i+1).getKey()) ).getWeight();
			}
			
		}
		return sum;
	}
	
	
	
	/**
	 * generates a of size NxN where each element holds the distance of the fastest path between 2 nodes 
	 * and the path itself
	 * @return  ArrayList<ArrayList< FloydElement >>
	 */
	private  ArrayList<ArrayList< FloydElement >> generateFloydWarshallMatrix() {
		return generateFloydWarshallMatrix(new LinkedList<NodeData>());
	}
	
	/**
	 * generates a matrix of size NxN where each element holds the distance of the fastest path between 2 nodes
	 * that contains all nodes given in the list and the path itself
	 * @param req - List of nodes that must be in the fastest route
	 * @return ArrayList<ArrayList< FloydElement >>
	 */
	private ArrayList<ArrayList< FloydElement >> generateFloydWarshallMatrix( List<NodeData> req){
		
		//current matrix
		ArrayList<ArrayList< FloydElement > > matrix =
				new ArrayList<ArrayList<FloydElement>>(this.nodes.size());
		//matrix from the last iteration
		ArrayList<ArrayList<FloydElement>> lastMatrix = 
				new ArrayList<ArrayList<FloydElement>>();
		//we start with the base matrix
		for(int src = 0; src < this.nodes.size(); src++) {
			lastMatrix.add(new ArrayList<FloydElement>() );
			for(int dest = 0; dest<this.nodes.size(); dest++) {
				if(this.nodes.get(src).getChildren().containsKey( this.nodes.get(dest) )) {
					LinkedList<NodeData> path = new LinkedList<NodeData>();
					path.add(this.nodes.get(src).get());
					path.add(this.nodes.get(dest).get());
					double dist = pathLength(path);
					lastMatrix.get(src).add(new FloydElement(path,dist) );
				}
				else {
					lastMatrix.get(src).add(new FloydElement(new LinkedList<NodeData>(),-1));
				}
			}
		}
		
		//for each vertex we update the matrix like so
		//if path_a_to_b is larger than path_a_to_vertex + path_vertex_to_b, we change path_a_to_b to path_a_to_vertex + path_vertex_to_b
		//unless the vertex is given as a requirement, in that case we will change path_a_to_b to path_a_to_vertex + path_vertex_to_b
		//regardless
		for(int matrixNum = 0; matrixNum < this.nodes.size(); matrixNum++) {
			matrix = new ArrayList<ArrayList<FloydElement>>(this.nodes.size());
			
			//find path_i_to_matrixNum and path_matrixNum_to_j for every vertex i and j on the graph
			for(int i = 0; i<this.nodes.size(); i++) {
				matrix.set(i, new ArrayList<FloydElement>(this.nodes.size()));
				matrix.get(i).set(matrixNum, lastMatrix.get(i).get(matrixNum));
			}
			matrix.set(matrixNum, new ArrayList<FloydElement>(lastMatrix.get(matrixNum)));
			
			//update the matrix
			for(int src = 0; src < this.nodes.size(); src++) {
				if(src != matrixNum) {
					for(int dest = 0; dest<this.nodes.size(); dest++) {
						if(dest != matrixNum) {
							FloydElement half1 = matrix.get(src).get(matrixNum);
							FloydElement half2 = matrix.get(matrixNum).get(dest);
							//if a path doesn't exists between the 2 halves then keep the old value 
							if(half1.getValue() == -1 || half2.getValue() == -1) {
								matrix.get(src).set(dest, new FloydElement(lastMatrix.get(src).get(dest)));
							}
							//if a path does exist update the path iff this node is in the required list or this node makes the path shorter or 
							//a path didn't exist previously
							else if(req.contains(this.nodes.get(matrixNum).get())  || 
									half1.add(half2).getValue() < lastMatrix.get(src).get(dest).getValue() ||
									lastMatrix.get(src).get(dest).getValue() == -1
									){
								
								matrix.get(src).set(dest, half1.add(half2));
							}
							//keep the old value if the old path is faster
							else {
								matrix.get(src).set(dest, new FloydElement(lastMatrix.get(src).get(dest)));
							}
						}
					}
				}
			}
			//update last matrix
			for(int i = 0; i<this.nodes.size(); i++) {
				lastMatrix.set(i, new ArrayList<FloydElement>(matrix.get(i)));
			}
			
		}
		return matrix;
	}
	
	
	
	@Override
	public NodeData center() {
		//return null if isn't connected graph
		if(!isConnected()) {
			return null;
		}
		
		ArrayList<Double> distances = new ArrayList<Double>(this.nodes.size());
		//find for each node what is the maximum distance between itself and every other node
		for(int src = 0; src<this.nodes.size(); src++) {
			distances.set(src,(double) -1);
			for(int dest = 0; dest<this.nodes.size(); dest++) {
				distances.set( src, Math.max(distances.get(src),this.floydMatrix.get(src).get(dest).getValue()) );
			}
		}
		
		//the node with the minimum, maximum distance to all other nodes is our center
		//graph is connected so no need to check that distances[i] == -1
		int nodeIdx = 0;
		for(int i = 0; i<this.nodes.size(); i++) {	
			if(distances.get(i) < distances.get(nodeIdx)) {
				nodeIdx = i;
			}
		}
		
		
		return this.nodes.get(nodeIdx).get();
	}

	@Override
	public List<NodeData> tsp(List<NodeData> cities) {
		//generateFloydWarshallMatrix(req) makes a floyd matrix such that each path contains the requirements
		ArrayList<ArrayList< FloydElement >> floydMatrix_requirements = generateFloydWarshallMatrix(cities);
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
		// TODO Auto-generated method stub
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
