package api_implementation;

import java.io.FileWriter;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import api.EdgeData;
import api.NodeData;

public class Algorithems implements DirectedWeightedGraphAlgorithms {
	
	
	DirectedWeightedGraph graph;
	
	public Algorithems(String json) {
		load(json);
	}
	public Algorithems(DirectedWeightedGraph g) {
		init(g);
	}
	
	@Override
	public void init(DirectedWeightedGraph g) {
		this.graph = g;
		
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
		int n = this.graph.nodeIter().next().getKey();
		HashMap<Integer,Boolean> visited = new HashMap<Integer,Boolean>();
		for(Iterator<NodeData> it = this.graph.nodeIter(); it.hasNext();) {
			visited.put(it.next().getKey(),false);
		}
		dfs(n, visited, true);
		
		for(Boolean flg: visited.values()) {
			if(!flg) {
				return false;
			}
		}
		for(Iterator<NodeData> it = this.graph.nodeIter(); it.hasNext();) {
			visited.put(it.next().getKey(),false);
		}
		dfs(n, visited, false);
		
		for(Boolean flg: visited.values()) {
			if(!flg) {
				return false;
			}
		}
		return true;
	}
	/**
	 * does a dfs search on the graph to detect node visited or not
	 * @param node - current node key
	 * @param visited - key -> node , value -> node has been visited
	 * @param parent2child - direction of traversal
	 */
	private void dfs(int node, HashMap<Integer,Boolean> visited, boolean parent2child) {
		visited.put(node, true);
		if(parent2child) {
			for(Iterator<EdgeData> it = this.graph.edgeIter(node); it.hasNext();) {
				EdgeData edge = it.next();
				if(!visited.get(edge.getDest())) {
					dfs(edge.getDest(),visited,parent2child);
				}
			}
		}
		else {
			for(Iterator<EdgeData> it = this.graph.edgeIter(); it.hasNext();) {
				EdgeData edge = it.next();
				if(edge.getDest() == node) {
					if(!visited.get(edge.getSrc())) {
						dfs(edge.getSrc(),visited,parent2child);
					}
				}
			}
		}
	}
	@Override
	public double shortestPathDist(int src, int dest) {
		List<NodeData> path = shortestPath(src,dest);
		if(path == null) {
			return -1;
		}
		return pathLength(path);
	}
	@Override
	public List<NodeData> shortestPath(int src, int dest) {
		List<NodeData> lst = new LinkedList<NodeData>();
		
		List<NodeData> bestList = new LinkedList<NodeData>();
		return shortestPath(graph.getNode(src),graph.getNode(dest), lst, bestList);
	}
	
	private List<NodeData> shortestPath(NodeData src, NodeData dest, List<NodeData> lst, List<NodeData> bestList) {
		Iterator<EdgeData> it = graph.edgeIter(src.getKey());
		
		if(src.getKey() == dest.getKey()) {
			lst.add(src);
			return lst;
		}
		else if(lst.contains(src) || !it.hasNext()){
			return null;
		}
		lst.add(src);
		for(it = graph.edgeIter(src.getKey()); it.hasNext();) {
			EdgeData edge = it.next();
			List<NodeData> temp = new LinkedList<NodeData>(lst);
			temp = shortestPath(graph.getNode(edge.getDest()),dest, temp, bestList);
			if(temp != null && (bestList.size() == 0 || pathLength(temp) < pathLength(bestList))) {
				bestList = temp;
			}
			
			
			
		}
		return bestList;
	}
	
	/**
	 * Calculates the total weight of the path in lst
	 * @param lst - a list of nodes
	 * @return double
	 */
	private double pathLength(List<NodeData> lst) {
		if(lst.size()<2) {
			return -1;
		}
		double sum = 0;
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
	 * @return  ArrayList<ArrayList< Double >>
	 */
	private HashMap<Integer,HashMap<Integer,Double>> generateFloydWarshallMatrix(){
		//current matrix
		HashMap<Integer,HashMap< Integer,Double >> matrix =
				new HashMap<Integer,HashMap< Integer,Double >>();
		//matrix from the last iteration
		HashMap<Integer,HashMap< Integer,Double >> lastMatrix = 
				new HashMap<Integer,HashMap< Integer,Double >>();
		//we start with the base matrix
		for(Iterator<NodeData> iter1 = this.graph.nodeIter(); iter1.hasNext();) {
			int src = iter1.next().getKey();
			lastMatrix.put(src,new HashMap<Integer,Double>() );
			for(Iterator<NodeData> iter2 = this.graph.nodeIter(); iter2.hasNext();) {
				int dest = iter2.next().getKey();
				EdgeData edge = this.graph.getEdge(src, dest);
				if(edge!=null) {
					LinkedList<NodeData> path = new LinkedList<NodeData>();
					path.add(this.graph.getNode(src));
					path.add(this.graph.getNode(dest));
					double dist = edge.getWeight();
					lastMatrix.get(src).put(dest,dist);
				}
				
			}
		}
		
		//for each vertex we update the matrix like so
		//if path_a_to_b is larger than path_a_to_vertex + path_vertex_to_b, we change path_a_to_b to path_a_to_vertex + path_vertex_to_b
		//unless the vertex is given as a requirement, in that case we will change path_a_to_b to path_a_to_vertex + path_vertex_to_b
		//regardless
		for(Iterator<NodeData> iter1 = this.graph.nodeIter(); iter1.hasNext();) {
			
			int matrixNum = iter1.next().getKey();
			matrix = new HashMap<Integer,HashMap<Integer,Double>>();
			
			//find path_i_to_matrixNum and path_matrixNum_to_j for every vertex i and j on the graph
			for(Iterator<NodeData> iter2 = this.graph.nodeIter(); iter2.hasNext();) {
				int i = iter2.next().getKey();
				if(lastMatrix.containsKey(i) && lastMatrix.get(i).containsKey(matrixNum)){//
					matrix.put(i, new HashMap<Integer,Double>());
					matrix.get(i).put(matrixNum, lastMatrix.get(i).get(matrixNum));
				}
			}
			matrix.put(matrixNum, new HashMap<Integer,Double>(lastMatrix.get(matrixNum)));
			
			//update the matrix
			for(Iterator<NodeData> iter2 = this.graph.nodeIter(); iter2.hasNext();) {
				int src = iter2.next().getKey();
				if(src != matrixNum) {
					for(Iterator<NodeData> iter3 = this.graph.nodeIter(); iter3.hasNext();) {
						int dest = iter3.next().getKey();
						if(dest != matrixNum) {
							Double half1 = null;
							Double half2 = null;
							if(matrix.containsKey(src) && matrix.get(src).containsKey(matrixNum) ) {
								half1 = matrix.get(src).get(matrixNum);
							}
							if(matrix.containsKey(matrixNum) && matrix.get(matrixNum).containsKey(dest) ) {
								half2 = matrix.get(matrixNum).get(dest);
							}
							//if src_to_matrixNum or matrixNum_to_dest doesn't exist
							if( half1 == null || half2 == null) {
								//if last matrix did contain a path, set it, else do nothing
								if(lastMatrix.containsKey(src) && lastMatrix.get(src).containsKey(dest)) {
									if(!matrix.containsKey(src)) {
										matrix.put(src, new HashMap<Integer,Double>());
									}
									matrix.get(src).put(dest, lastMatrix.get(src).get(dest));
								}
							}
							//add the new path if we previously didn't have a path
							else if(!(lastMatrix.containsKey(src) && lastMatrix.get(src).containsKey(dest))) {
								if(!matrix.containsKey(src)) {
									matrix.put(src, new HashMap<Integer,Double>());
								}
								matrix.get(src).put(dest,half1 + half2);
							}
							//add the fastest path between the old one and the new one
							else{
								if(!matrix.containsKey(src)) {
									matrix.put(src, new HashMap<Integer,Double>());
								}
								matrix.get(src).put(dest, Math.min(half1+half2, lastMatrix.get(src).get(dest)));
							}
						}
					}
				}
			}
			//update last matrix
			for(Integer i: matrix.keySet()) {
				lastMatrix.put(i, new HashMap<Integer,Double>(matrix.get(i)));
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
		HashMap<Integer,HashMap<Integer,Double>> floydMatrix = generateFloydWarshallMatrix();
		HashMap<Integer,Double> distances = new HashMap<Integer,Double>();
		//find for each node what is the maximum distance between itself and every other node
		for(Iterator<NodeData> srcIter = this.graph.nodeIter(); srcIter.hasNext();) {
			int src = srcIter.next().getKey();
			distances.put(src,(double) -1);
			for(Iterator<NodeData> destIter = this.graph.nodeIter(); destIter.hasNext();) {
				int dest = destIter.next().getKey();
				//distances.set( src, Math.max(distances.get(src),this.floydMatrix.get(src).get(dest).getValue()) );
				distances.put( src, Math.max(distances.get(src),floydMatrix.get(src).get(dest) ));
			}
		}
		
		//the node with the minimum, maximum distance to all other nodes is our center
		//graph is connected so no need to check that distances[i] == -1
		int nodeIdx = 0;
		for(Iterator<NodeData> iter = this.graph.nodeIter(); iter.hasNext();) {
			int i = iter.next().getKey();
			if(distances.get(i) < distances.get(nodeIdx)) {
				nodeIdx = i;
			}
		}
		
		
		return this.graph.getNode(nodeIdx);
	}

	@Override
	public List<NodeData> tsp(List<NodeData> cities) {		
		if(cities.size() < 1) {
			return new LinkedList<NodeData>();
		}
		double bestLen = Double.MAX_VALUE;
		List<NodeData> bestlst = new LinkedList<NodeData>();
		for(int i = 0; i<cities.size(); i++) {
			List<NodeData> temp = new LinkedList<NodeData>(cities);
			temp.remove(i);
			List<NodeData> c =  g(cities.get(i).getKey(),temp);
			double len = pathLength(c);
			if(len < bestLen) {
				bestLen = len;
				bestlst = c;
			}
		}
		List<NodeData> best = new LinkedList<NodeData>();
		for(int i = 0; i<bestlst.size(); i++) {
			best.add(bestlst.get(i));
			if(i<bestlst.size()-1 && bestlst.get(i).getKey() == bestlst.get(i+1).getKey()) {
				i++;
			}
		}
		return best;
	}
	
	
	
	private List<NodeData> g(int idx, List<NodeData> nodes){
		
		if(nodes.size() < 1) {
			return new LinkedList<NodeData>();
		}
		double bestLen = Double.MAX_VALUE;
		List<NodeData> bestlst = new LinkedList<NodeData>();
		for(int i = 0; i<nodes.size(); i++) {
			List<NodeData> c = shortestPath(idx,nodes.get(i).getKey());
			List<NodeData> temp = new LinkedList<NodeData>(nodes);
			temp.remove(i);
			c.addAll( g(nodes.get(i).getKey(),temp));
			double len = pathLength(c);
			if(len < bestLen) {
				bestLen = len;
				bestlst = c;
			}
			
		}
		return bestlst;
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
