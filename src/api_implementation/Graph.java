package api_implementation;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.google.gson.*;


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
	
	
	
	private HashMap<Integer, NodeStructure> nodes;
	private int edgeSize;
	//detect what node gets changed
	private HashMap<Integer, Integer> lastNodeChange;
	//detects changes in the amount of nodes
	private int lastGraphChange;
	//detects changes in the amount of edges
	private int lastEdgeChange;
	
	public Graph(List<NodeData> nodeList, List<EdgeData> edgeList) {
		init(nodeList,edgeList);
	}
	
	/**
	 * Copy constructor
	 * @param g DirectedWeightedGraph to copy
	 */
	public Graph(DirectedWeightedGraph g) {
		this.nodes = new HashMap<Integer, NodeStructure>();
		this.edgeSize = 0;
		this.lastGraphChange = 0;
		this.lastNodeChange = new HashMap<Integer, Integer>();
		
		for(Iterator<NodeData> n = g.nodeIter(); n.hasNext(); ) {
			this.nodes.put(n.next().getKey(), new NodeStructure(n.next()));
			lastNodeChange.put(n.next().getKey(),0);
		}
		for(Iterator<EdgeData> e = g.edgeIter(); e.hasNext(); ) {
			this.nodes.get(e.next().getSrc()).addChild(e.next().getDest(),e.next());
			this.nodes.get(e.next().getDest()).addParent(e.next().getSrc(),e.next());
			this.edgeSize++;
		}
		
	}
	/**
	 * Constructor from json file
	 * @param json_file - json file name in data/
	 */
	public Graph(String json_file) {
		List<EdgeData> edgeDataList = new LinkedList<EdgeData>();
		List<NodeData> nodeDataList = new LinkedList<NodeData>();
		try {
			Gson gson = new Gson();
			Reader reader = Files.newBufferedReader(Paths.get(json_file));
			
			Map<?,?> json = gson.fromJson(reader,Map.class);
			List<?> edges = (List<?>) json.get("Edges");
			List<?> nodes = (List<?>) json.get("Nodes");
			
			for(Object obj: edges) {
				Map<?,?> objMap = (Map<?,?>)obj;
				int src = ((Double) objMap.get("src")).intValue();
				double w = (double)objMap.get("w");
				int dest = ((Double) objMap.get("dest")).intValue();
				edgeDataList.add(new Edge(src,dest,w));
			}
			for(Object obj: nodes) {
				Map<?,?> objMap = (Map<?,?>)obj;
				String[] pos = ((String) objMap.get("pos")).split(",");
				double x = Double.parseDouble(pos[0]);
				double y = Double.parseDouble(pos[1]);
				double z = Double.parseDouble(pos[2]);
				int id = ((Double) objMap.get("id")).intValue();
				NodeData n = new Node(id,new Point(x,y,z));
				nodeDataList.add(n);
				
			}
			
			
			reader.close();
		}
		catch (Exception ex) {
		    ex.printStackTrace();
		}
		
		init(nodeDataList,edgeDataList);
	}
	
	/**
	 * initiate our Graph
	 * @param nodeList - Linked list of all the nodes on the graph
	 * @param edgeList - Linked list of all the edges on the graph
	 */
	private void init(List<NodeData> nodeList, List<EdgeData> edgeList) {
		this.nodes = new HashMap<Integer, NodeStructure>();
		
		this.edgeSize = 0;
		this.lastGraphChange = 0;
		this.lastNodeChange = new HashMap<Integer, Integer>();
		for(NodeData n: nodeList) {
			this.nodes.put(n.getKey(), new NodeStructure(n));
			lastNodeChange.put(n.getKey(),0);
		}
		for(EdgeData e: edgeList) {
			this.nodes.get(e.getSrc()).addChild(e.getDest(),e);
			this.nodes.get(e.getDest()).addParent(e.getSrc(),e);
			this.edgeSize++;
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
		
		this.nodes.put(n.getKey(), new NodeStructure(n));
		this.lastNodeChange.put(n.getKey(), 0);
		this.lastGraphChange++;
	}

	@Override
	public void connect(int src, int dest, double w) {
		if(this.nodes.containsKey(src) && this.nodes.containsKey(dest) && !this.nodes.get(src).getChildren().containsKey(dest)) {
			this.edgeSize++;
			Edge e = new Edge(src,dest,w);
			this.nodes.get(src).addChild(dest, e);
			this.nodes.get(dest).addParent(src, e);
			this.lastNodeChange.put(src, lastNodeChange.get(src)+1);
			this.lastNodeChange.put(dest, lastNodeChange.get(dest)+1);
			this.lastEdgeChange++;
		}
	}

	@Override
	public Iterator<NodeData> nodeIter() throws RuntimeException{
		Iterator<NodeData> iter = new Iterator<NodeData>() {
			private Iterator<Integer> it = nodes.keySet().iterator();
			private int changeStamp = lastGraphChange;
			int idx;
			private void validate() throws RuntimeException{
				if(this.changeStamp != lastGraphChange) {
					throw new RuntimeException();
				}
			}
			@Override
			public boolean hasNext() {
				validate();
				return it.hasNext();
			}
			@Override
			public NodeData next() {
				validate();
				idx = it.next();
				return nodes.get(idx).get();
			}
			
			@Override
			public void remove() {
				validate();
				removeEdge(idx);
				lastNodeChange.remove(idx);
				this.changeStamp = lastGraphChange;
				it.remove();
				
			}
			@Override
			public void forEachRemaining(Consumer<? super NodeData> action) {
				it.forEachRemaining( idx ->  action.accept(nodes.get(idx).get()));
			}
			
		};
		
		return iter;
	}

	@Override
	public Iterator<EdgeData> edgeIter() throws RuntimeException{
		Iterator<EdgeData> iter = new Iterator<EdgeData>() {
			private Iterator<NodeData> nodeIt = nodeIter();
			private int src;
			private Iterator<EdgeData> edgeIt = null;
			private int changeStamp = lastEdgeChange;
			
			private void validate() throws RuntimeException{
				if(this.changeStamp != lastEdgeChange) {
					throw new RuntimeException();
				}
			}
			@Override
			public boolean hasNext() {
				validate();
				
				if( this.edgeIt != null) {
					if(this.edgeIt.hasNext()) {
						return true;
					}
					while(!edgeIt.hasNext() && nodeIt.hasNext()) {
						this.src = nodeIt.next().getKey();
						this.edgeIt = edgeIter(this.src);
					}
					return edgeIt.hasNext();
				}
				else {
					while(nodeIt.hasNext()) {
						this.src = nodeIt.next().getKey();
						this.edgeIt = edgeIter(this.src);
						if(edgeIt.hasNext()) {
							return true;
						}
					}
				}
				
				return false;
			}
			@Override
			public EdgeData next() {
				validate();
				while(this.nodeIt.hasNext() && (this.edgeIt == null || !this.edgeIt.hasNext())) {
					this.src = nodeIt.next().getKey();
					this.edgeIt = edgeIter(this.src);
				}
				return this.edgeIt.next();
			}
			
			@Override
			public void remove() {
				validate();
				this.edgeIt.remove();
				this.changeStamp = lastEdgeChange;
				
			}
			@Override
			public void forEachRemaining(Consumer<? super EdgeData> action) {
				validate();
				while(hasNext()) {
					action.accept(next());
				}
					
					
			}
		};
		return iter;
	}

	@Override
	public Iterator<EdgeData> edgeIter(int node_id) throws RuntimeException{
		Iterator<EdgeData> iter = new Iterator<EdgeData>() {
			private int src = node_id;
			private int idx;
			private Iterator<Integer> edgeIt = nodes.get(src).getChildren().keySet().iterator();
			private int changeStamp = lastNodeChange.get(src);
			
			private void validate() throws RuntimeException{
				if(this.changeStamp != lastNodeChange.get(src)) {
					throw new RuntimeException();
				}
			}
			@Override
			public boolean hasNext() {
				validate();
				return edgeIt.hasNext();
			}
			@Override
			public EdgeData next() {
				validate();
				idx = edgeIt.next();
				return nodes.get(src).getChildren().get(idx);
			}
			
			@Override
			public void remove() {
				validate();
				nodes.get(idx).getChildren().remove(src);
				edgeSize--;
				lastNodeChange.put(src, lastNodeChange.get(src)+1);
				lastNodeChange.put(idx, lastNodeChange.get(idx)+1);
				lastEdgeChange++;
				changeStamp = lastNodeChange.get(src);
				edgeIt.remove();
				
				
			}
			@Override
			public void forEachRemaining(Consumer<? super EdgeData> action) {
				edgeIt.forEachRemaining(dest -> 
					action.accept(nodes.get(src).getChildren().get(dest))
					
				);
				
					
			}
		};
		return iter;
	}

	@Override
	public NodeData removeNode(int key) {
		if(!this.nodes.containsKey(key))
			return null;
		
		removeEdge(key);
		this.lastNodeChange.remove(key);
		this.lastGraphChange++;
		return this.nodes.remove(key).get();
	}
	private void removeEdge(int key) {
		Set<Integer> nodeParents = this.nodes.get(key).getParents().keySet();
		Set<Integer> nodeChildren = this.nodes.get(key).getChildren().keySet();
		for(Integer src: nodeParents) {
			removeEdge(src,key);
		}
		for(Integer dest: nodeChildren) {
			removeEdge(key,dest);
		}
	}

	@Override
	public EdgeData removeEdge(int src, int dest) {
		if(this.nodes.containsKey(src) && this.nodes.containsKey(dest) && this.nodes.get(src).getChildren().containsKey(dest)) {
			
			this.nodes.get(src).getChildren().remove(dest);
			this.edgeSize--;
			this.lastNodeChange.put(src, lastNodeChange.get(src)+1);
			this.lastNodeChange.put(dest, lastNodeChange.get(dest)+1);
			this.lastEdgeChange++;
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
		
		//Find the node with the highest amount of children and parents 
		for(Map.Entry<Integer, NodeStructure> entry: this.nodes.entrySet()) {
			if(maxEdges < entry.getValue().getChildren().size() + entry.getValue().getParents().size()) {
				maxEdges = entry.getValue().getChildren().size() + entry.getValue().getParents().size();
				
			}
		}
		return maxEdges;
	}
	
	
	@Override
	public String toString() {
		ArrayList<String> edge_arr = new ArrayList<String>();
		ArrayList<String> node_arr = new ArrayList<String>();
		Iterator<NodeData> nodeIter = nodeIter();
		Iterator<EdgeData> edgeIter = edgeIter();
		while(edgeIter.hasNext()) {
			edge_arr.add(edgeIter.next().toString());
		}
		while(nodeIter.hasNext()) {
			node_arr.add(nodeIter.next().toString());
		}
		HashMap<String,String> out = new HashMap<String,String>();
		out.put("Edges", edge_arr.toString());
		out.put("Nodes",node_arr.toString());
		String s = out.toString();
		
		return s.replace("=", ":");
	}

}
