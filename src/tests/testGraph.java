package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;

import api.DirectedWeightedGraph;
import api.EdgeData;
import api.GeoLocation;
import api.NodeData;
import api_implementation.Edge;
import api_implementation.Graph;
import api_implementation.Node;
import api_implementation.Point;

class testGraph {
	
	@Test
	void test_getNode() {
		 GeoLocation p1 = new Point(1,2,0);
		 GeoLocation p2 = new Point(7,4,0);
		 GeoLocation p3 = new Point(8,3,0);
		 GeoLocation p4 = new Point(12,8,0);
		 NodeData n1 = new Node(0,p1);
		 NodeData n2 = new Node(4,p2);
		 NodeData n3 = new Node(1,p3);
		 NodeData n4 = new Node(2,p4);
		 LinkedList<NodeData> nodes = new LinkedList<NodeData>();
		 LinkedList<EdgeData> edges = new LinkedList<EdgeData>();
		 nodes.add(n1);
		 nodes.add(n2);
		 nodes.add(n3);
		 nodes.add(n4);
		 DirectedWeightedGraph g = new Graph(nodes,edges);
		 //test that we actually get a node
		 assertTrue(g.getNode(0).equals(n1));
		 //test that the nodes we get are returned by the node's key value and not the order inputed
		 assertTrue(g.getNode(1).equals(n3));
		 assertTrue(g.getNode(2).equals(n4));
		 //test that the program returns null if no node was found
		 assertTrue(g.getNode(3) == null);
		 assertTrue(g.getNode(4).equals(n2));
		 
		
	}
	@Test
	void test_getEdge() {
		GeoLocation p1 = new Point(1,2,0);
		GeoLocation p2 = new Point(7,4,0);
		GeoLocation p3 = new Point(8,3,0);
		GeoLocation p4 = new Point(12,8,0);
		NodeData n1 = new Node(0,p1);
		NodeData n2 = new Node(4,p2);
		NodeData n3 = new Node(1,p3);
		NodeData n4 = new Node(2,p4);
		LinkedList<NodeData> nodes = new LinkedList<NodeData>();
		nodes.add(n1);
		nodes.add(n2);
		nodes.add(n3);
	 	nodes.add(n4);
	 	LinkedList<EdgeData> edges = new LinkedList<EdgeData>();
	 	EdgeData e1 = new Edge(1,4,4.15);
	 	EdgeData e2 = new Edge(4,1,4.15);
	 	EdgeData e3 = new Edge(1,2,1);
	 	edges.add(e1);
	 	edges.add(e2);
	 	edges.add(e3);
	 	DirectedWeightedGraph g = new Graph(nodes,edges);
	 	//test that we actually get an Edge, and check that the src/dest order is important
		assertTrue(g.getEdge(1, 4).equals(e1) && !g.getEdge(1, 4).equals(e2));
		assertTrue(g.getEdge(1, 2).equals(e3));
		//test that the program returns null if no node was found, even if the opposite node exists
		assertTrue(g.getEdge(2, 1)==null);
		
	}
	
	@Test
	void test_NodeIterator() {
		GeoLocation p1 = new Point(1,2,0);
		GeoLocation p2 = new Point(7,4,0);
		GeoLocation p3 = new Point(8,3,0);
		NodeData n1 = new Node(0,p1);
		NodeData n2 = new Node(4,p2);
		NodeData n3 = new Node(1,p3);
		LinkedList<NodeData> nodes = new LinkedList<NodeData>();
		LinkedList<EdgeData> edges = new LinkedList<EdgeData>();
		nodes.add(n1);
		nodes.add(n2);
		
		DirectedWeightedGraph g = new Graph(nodes,edges);
		Iterator<NodeData> nodeIter = g.nodeIter();
		int counter = 0;
		//test iterator
		while(nodeIter.hasNext()) {
			assertTrue(nodes.remove(nodeIter.next()));
			counter++;
		}
		assertTrue(counter == 2);
		assertTrue(g.nodeSize()==2);//testing node size 
		nodeIter = g.nodeIter();
		//testing add node
		g.addNode(n3);
		assertTrue(g.nodeSize()==3);//testing node size gets updated
		//test that after changes to the graph are made, we cant use any iterators made before the change
		try {
			nodeIter.hasNext();
			assertTrue(false);
		}
		catch(RuntimeException e){
			assertTrue(true);
		}
		nodeIter = g.nodeIter();
		System.out.println("should print 3 node id's:");
		nodeIter.forEachRemaining(o -> System.out.println(o.getKey()));
		
		
		nodeIter = g.nodeIter();
		nodeIter.next();
		System.out.println("should print 2 node id's:");
		nodeIter.forEachRemaining(o -> System.out.println(o.getKey()));
		 
		nodeIter = g.nodeIter();
		nodeIter.next();
		nodeIter.remove();
		assertTrue(g.nodeSize()==2);//testing that iterator remove can be done more than once, and updates the graph 
		nodeIter.next();
		nodeIter.remove();
		assertTrue(g.nodeSize()==1);
		
 
	}
	
	
	@Test
	void test_EdgeIterator() {
		GeoLocation p1 = new Point(1,2,0);
		GeoLocation p2 = new Point(7,4,0);
		GeoLocation p3 = new Point(8,3,0);
		GeoLocation p4 = new Point(12,8,0);
		NodeData n1 = new Node(0,p1);
		NodeData n2 = new Node(4,p2);
		NodeData n3 = new Node(1,p3);
		NodeData n4 = new Node(2,p4);
		LinkedList<NodeData> nodes = new LinkedList<NodeData>();
		nodes.add(n1);
		nodes.add(n2);
		nodes.add(n3);
	 	nodes.add(n4);
	 	LinkedList<EdgeData> edges = new LinkedList<EdgeData>();
	 	EdgeData e1 = new Edge(1,4,4.15);
	 	EdgeData e2 = new Edge(4,1,3.75);
	 	
	 	edges.add(e1);
	 	edges.add(e2);
	 	
		
		DirectedWeightedGraph g = new Graph(nodes,edges);
		Iterator<EdgeData> edgeIter = g.edgeIter();
		int counter = 0;
		//test iterator
		while(edgeIter.hasNext()) {
			
			assertTrue(edges.remove(edgeIter.next()));
			counter++;
		}
		assertTrue(counter == 2);
		assertTrue(g.edgeSize()==2);//testing edgeSize
		edgeIter = g.edgeIter();
		//testing connect
		g.connect(1,2,1);
		assertTrue(g.edgeSize()==3);//testing edge size gets updated
		//test that after changes to the graph are made, we cant use any iterators made before the change
		try {
			edgeIter.hasNext();
			assertTrue(false);
		}
		catch(RuntimeException e){
			assertTrue(true);
		}
		edgeIter = g.edgeIter();
		System.out.println("should print 3 edge weights:");
		edgeIter.forEachRemaining(o -> System.out.println(o.getWeight()));
		
		
		edgeIter = g.edgeIter();
		edgeIter.next();
		System.out.println("should print 2 edge weights:");
		edgeIter.forEachRemaining(o -> System.out.println(o.getWeight()));
		 
		edgeIter = g.edgeIter();
		edgeIter.next();
		edgeIter.remove();
		assertTrue(g.edgeSize()==2);//testing that iterator remove can be done more than once, and updates the graph 
		edgeIter.next();
		edgeIter.remove();
		assertTrue(g.edgeSize()==1);
		
		
	}
	@Test
	void test_EdgeIterator2() {
		GeoLocation p1 = new Point(1,2,0);
		GeoLocation p2 = new Point(7,4,0);
		GeoLocation p3 = new Point(8,3,0);
		GeoLocation p4 = new Point(12,8,0);
		NodeData n1 = new Node(0,p1);
		NodeData n2 = new Node(4,p2);
		NodeData n3 = new Node(1,p3);
		NodeData n4 = new Node(2,p4);
		LinkedList<NodeData> nodes = new LinkedList<NodeData>();
		nodes.add(n1);
		nodes.add(n2);
		nodes.add(n3);
	 	nodes.add(n4);
	 	LinkedList<EdgeData> edges = new LinkedList<EdgeData>();
	 	EdgeData e1 = new Edge(1,4,4.15);
	 	EdgeData e2 = new Edge(1,2,3.75);
	 	EdgeData e3 = new Edge(2,1,8.3);
	 	
	 	edges.add(e1);
	 	edges.add(e2);
	 	edges.add(e3);
		
		DirectedWeightedGraph g = new Graph(nodes,edges);
		Iterator<EdgeData> edgeIter = g.edgeIter(1);
		int counter = 0;
		//test iterator
		while(edgeIter.hasNext()) {
			
			assertTrue(edges.remove(edgeIter.next()));
			counter++;
		}
		assertTrue(counter == 2);
		
		edgeIter = g.edgeIter(1);
		//testing connect
		g.connect(0,2,1);
		
		//test that after changes to the graph are made, we can use the iterator only if the edge was unaffected
		try {
			edgeIter.hasNext();
			assertTrue(true);
		}
		catch(RuntimeException e){
			assertTrue(false);
		}
		g.connect(1, 0, 5.3);
		//test that after changes to the graph are made, we can use the iterator only if the edge was unaffected
		try {
			edgeIter.hasNext();
			assertTrue(false);
		}
		catch(RuntimeException e){
			assertTrue(true);
		}
		
		edgeIter = g.edgeIter(1);
		System.out.println("should print 3 edge weights:");
		edgeIter.forEachRemaining(o -> System.out.println(o.getWeight()));
		
		
		edgeIter = g.edgeIter(1);
		edgeIter.next();
		System.out.println("should print 2 edge weights:");
		edgeIter.forEachRemaining(o -> System.out.println(o.getWeight()));
		 
		edgeIter = g.edgeIter();
		edgeIter.next();
		edgeIter.remove();
		assertTrue(g.edgeSize()==4);//testing that iterator remove can be done more than once, and updates the graph 
		edgeIter.next();
		edgeIter.remove();
		assertTrue(g.edgeSize()==3);
		
		
	}
	

}
