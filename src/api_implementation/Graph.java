package api_implementation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import api.DirectedWeightedGraph;
import api.EdgeData;
import api.NodeData;

public class Graph implements DirectedWeightedGraph {
	/**
	 * data structure that allows us to hold each node with its parents, children and the edge data between them
	 * 
	 * @author ziv morgan
	 *	
	 */
	private class NodeStructure{
		NodeData node;
		HashMap<Integer, EdgeData> parents;
		HashMap<Integer, EdgeData> children;
		
		NodeStructure(NodeData node){
			this.node = node;
			this.parents = new HashMap<Integer, EdgeData>();
			this.children = new HashMap<Integer, EdgeData>();
		}
		NodeData get() {
			return this.node;
		}
		void addParent(Integer n, EdgeData e) {
			this.parents.put(n, e);
		}
		void addChild(Integer n, EdgeData e) {
			this.children.put(n, e);
		}
		HashMap<Integer, EdgeData> getParents() {
			return this.parents;
		}
		HashMap<Integer, EdgeData> getChildren() {
			return this.children;
		}
	}
	
	
	
	HashMap<Integer, NodeStructure> nodes;
	LinkedList<NodeData> nodeList;
	LinkedList<EdgeData> edgeList;
	HashMap<Integer, Collection<EdgeData>> edgeMap;
	boolean graphChanged;
	int edgeSize;
	
	/**
	 * Copy constructor
	 * @param g DirectedWeightedGraph to copy
	 */
	public Graph(DirectedWeightedGraph g) {
		this.nodes = new HashMap<Integer, NodeStructure>();
		this.edgeMap = new HashMap<Integer, Collection<EdgeData>>();
		this.graphChanged = false;
		this.edgeSize = 0;
		
		for(Iterator<NodeData> n = g.nodeIter(); n.hasNext(); ) {
			this.nodes.put(n.next().getKey(), new NodeStructure(n.next()));
		}
		for(Iterator<EdgeData> e = g.edgeIter(); e.hasNext(); ) {
			this.nodes.get(e.next().getSrc()).addChild(e.next().getDest(),e.next());
			this.nodes.get(e.next().getDest()).addParent(e.next().getSrc(),e.next());
			this.edgeSize++;
		}
		for(Map.Entry<Integer, NodeStructure> entry: this.nodes.entrySet()) {
			this.edgeMap.put(entry.getKey(), entry.getValue().getChildren().values());
		}
	}
	
	
	/**
	 * Constructor for graph
	 * @param nodeList - Linked list of all the nodes on the graph
	 * @param edgeList - Linked list of all the edges on the graph
	 */
	public Graph(LinkedList<NodeData> nodeList, LinkedList<EdgeData> edgeList) {
		this.nodes = new HashMap<Integer, NodeStructure>();
		this.edgeMap = new HashMap<Integer, Collection<EdgeData>>();
		this.edgeList = edgeList;
		this.nodeList = nodeList;
		this.graphChanged = false;
		this.edgeSize = 0;
		
		for(NodeData n: nodeList) {
			this.nodes.put(n.getKey(), new NodeStructure(n));
		}
		for(EdgeData e: edgeList) {
			this.nodes.get(e.getSrc()).addChild(e.getDest(),e);
			this.nodes.get(e.getDest()).addParent(e.getSrc(),e);
			this.edgeSize++;
		}
		for(Map.Entry<Integer, NodeStructure> entry: this.nodes.entrySet()) {
			this.edgeMap.put(entry.getKey(), entry.getValue().getChildren().values());
		}
		
	}
	
	@Override
	public NodeData getNode(int key) {
		if(this.nodes.containsKey(key))
			return this.nodes.get(key).get();
		return null;
	}

	@Override
	public EdgeData getEdge(int src, int dest) {
		if(this.nodes.get(src).getChildren().containsKey(dest))
			return this.nodes.get(src).getChildren().get(dest);
		return null;
	}

	@Override
	public void addNode(NodeData n) {
		this.graphChanged = true;
		this.nodes.put(n.getKey(), new NodeStructure(n));
	}

	@Override
	public void connect(int src, int dest, double w) {
		if(this.nodes.containsKey(src) && this.nodes.containsKey(dest) && !this.nodes.get(src).getChildren().containsKey(dest)) {
			this.graphChanged = true;
			this.edgeSize++;
			this.nodes.get(src).addChild(dest, new Edge(src,dest,w));
			this.nodes.get(dest).addParent(src, new Edge(src,dest,w));
		}
	}

	@Override
	public Iterator<NodeData> nodeIter() throws RuntimeException{
		if(this.graphChanged) {
			throw new RuntimeException();
		}
		return this.nodeList.iterator();
	}

	@Override
	public Iterator<EdgeData> edgeIter() throws RuntimeException{
		if(this.graphChanged) {
			throw new RuntimeException();
		}
		return this.edgeList.iterator();
	}

	@Override
	public Iterator<EdgeData> edgeIter(int node_id) throws RuntimeException{
		Collection<EdgeData> collect = this.nodes.get(node_id).getChildren().values();
		if(!collect.equals(this.edgeMap.get(node_id))) {
			throw new RuntimeException();
		}
		return collect.iterator();
	}

	@Override
	public NodeData removeNode(int key) {
		if(!this.nodes.containsKey(key))
			return null;
		
		this.graphChanged = true;
		Set<Integer> nodeParents = this.nodes.get(key).getParents().keySet();
		Set<Integer> nodeChildren = this.nodes.get(key).getChildren().keySet();
		for(Integer src: nodeParents) {
			removeEdge(src,key);
		}
		for(Integer dest: nodeChildren) {
			removeEdge(key,dest);
		}
		
		return this.nodes.remove(key).get();
	}

	@Override
	public EdgeData removeEdge(int src, int dest) {
		if(this.nodes.containsKey(src) && this.nodes.containsKey(dest) && this.nodes.get(src).getChildren().containsKey(dest)) {
			this.graphChanged = true;
			this.nodes.get(src).getChildren().remove(dest);
			this.edgeSize--;
			return this.nodes.get(dest).getParents().remove(src);
		}
		
		return null;
	}

	@Override
	public int nodeSize() {
		return this.nodes.size();
	}

	@Override
	public int edgeSize() {
		return this.edgeSize;
	}

	@Override
	public int getMC() {
		int maxEdges = 0;
		//int maxEdgesIdx = 0;
		//Find the node with the highest amount of children and parents 
		for(Map.Entry<Integer, NodeStructure> entry: this.nodes.entrySet()) {
			if(maxEdges < entry.getValue().getChildren().size() + entry.getValue().getParents().size()) {
				maxEdges = entry.getValue().getChildren().size() + entry.getValue().getParents().size();
				//maxEdgesIdx = entry.getKey();
			}
		}
		return maxEdges;
	}

}
