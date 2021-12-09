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
	void test_NodeOperations() {
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
		 //testing add node
		 g.addNode(n3);
		 
		 assertTrue(g.nodeSize()==3);//testing node size gets updated
		 //test that after changes to the graph are made, we cant get an iterator back
		 try {
			 nodeIter = g.nodeIter();
			 assertTrue(false);
		 }
		 catch(RuntimeException e){
			 assertTrue(true);
		 }
		 
		 
		 counter = 0;
		 //test iterator
		 while(nodeIter.hasNext()) {
			 assertTrue(nodes.remove(nodeIter.next()));
			 counter++;
		 }
		 
		 
		 //testing get node
		 assertTrue(g.getNode(0).equals(n1));
		 assertTrue(g.getNode(1).equals(n3));
		 assertTrue(g.getNode(2).equals(n4));
		 assertTrue(g.getNode(3) == null);
		 assertTrue(g.getNode(4).equals(n2));
		 
		 
		 
	}
	
	

}
