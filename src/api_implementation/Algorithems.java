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
		boolean visited;
		HashMap<Tree, EdgeData> parents;
		HashMap<Tree, EdgeData> children;
		
		Tree(NodeData node){
			this.node = node;
			this.visited = false;
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
		void setVisited(boolean flg) {
			this.visited = flg;
		}
		HashMap<Tree, EdgeData> getParents() {
			return this.parents;
		}
		HashMap<Tree, EdgeData> getChildren() {
			return this.children;
		}
		boolean getVisited() {
			return this.visited;
		}
	}
	
	DirectedWeightedGraph graph;
	ArrayList<Tree> nodes;
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
		for(Tree t: this.nodes) {
			if(t.getParents().size() != this.nodes.size()-1 || t.getChildren().size() != this.nodes.size()-1) {
				return false;
			}
		}
		return true;
	}

	@Override
	public double shortestPathDist(int src, int dest) {
		
		return pathLength(shortestPath(src,dest));
	}

	@Override
	public List<NodeData> shortestPath(int src, int dest) {
		List<NodeData> lst = new LinkedList<NodeData>();
		List<NodeData> bestList = new LinkedList<NodeData>();
		return shortestPath(src,dest, lst, bestList);
	}
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

	private double pathLength(List<NodeData> lst) {
		double sum = -1;
		for(int i = 0; i<lst.size()-1; i++) {
			sum += this.nodes.get( lst.get(i).getKey()  ).getChildren().get( this.nodes.get(lst.get(i+1).getKey()) ).getWeight();
		}
		return sum;
	}

	@Override
	public NodeData center() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<NodeData> tsp(List<NodeData> cities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean save(String file) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean load(String file) {
		// TODO Auto-generated method stub
		return false;
	}

}
