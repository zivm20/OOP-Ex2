package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import api.DirectedWeightedGraphAlgorithms;
import api.NodeData;
import api_implementation.Algorithems;

class testAlgorithems {
	DirectedWeightedGraphAlgorithms not_connected1 = new Algorithems("data/not_connected1.json"),
			not_connected2 =  new Algorithems("data/not_connected2.json"), 
			not_connected3 =  new Algorithems("data/not_connected3.json"),
			not_connected4 =  new Algorithems("data/not_connected4.json"), 
			connected1 =  new Algorithems("data/connected1.json"), 
			diamond = new Algorithems("data/diamond.json"),
			faster_connected = new Algorithems("data/2-5-4faster.json"), 
			faster_not_connected = new Algorithems("data/2-5-4faster_notConnected.json");
	@Test
	void test_Connected() {
		assertTrue(!not_connected1.isConnected());
		assertTrue(!not_connected2.isConnected());
		assertTrue(!not_connected3.isConnected());
		assertTrue(!not_connected4.isConnected());
		assertTrue(connected1.isConnected());
		assertTrue(diamond.isConnected());
		assertTrue(faster_connected.isConnected());
		assertTrue(!faster_not_connected.isConnected());
	}
	@Test
	void test_shortestPathDist() {		
		assertTrue( diamond.shortestPathDist(0, 2) == diamond.getGraph().getEdge(0, 1).getWeight() + diamond.getGraph().getEdge(1, 2).getWeight());
		assertTrue( faster_connected.shortestPathDist(2, 4) == faster_connected.getGraph().getEdge(2, 5).getWeight()
				+ faster_connected.getGraph().getEdge(5, 4).getWeight());
		assertTrue( faster_not_connected.shortestPathDist(2, 4) == faster_not_connected.getGraph().getEdge(2, 5).getWeight()
				+ faster_not_connected.getGraph().getEdge(5, 4).getWeight());
		assertTrue(not_connected4.shortestPathDist(5,2)==-1);
	}
	@Test
	void test_tsp() {
		LinkedList<NodeData> req = new LinkedList<NodeData>();
		LinkedList<NodeData> exp = new LinkedList<NodeData>();
		req.add( faster_connected.getGraph().getNode(5));
		req.add( faster_connected.getGraph().getNode(0));
		req.add( faster_connected.getGraph().getNode(4));
		
		exp.add( faster_connected.getGraph().getNode(5));
		exp.add( faster_connected.getGraph().getNode(4));
		exp.add( faster_connected.getGraph().getNode(0));
		
		List<NodeData> ans = faster_connected.tsp(req);
		for(int i = 0; i<ans.size(); i++) {
			assertTrue(ans.get(i).getKey() == exp.get(i).getKey());
		}
		req = new LinkedList<NodeData>();
		exp = new LinkedList<NodeData>();
		req.add( faster_connected.getGraph().getNode(2));
		req.add( faster_connected.getGraph().getNode(3));
		req.add( faster_connected.getGraph().getNode(4));
		req.add( faster_connected.getGraph().getNode(1));
		
		exp.add( faster_connected.getGraph().getNode(3));
		exp.add( faster_connected.getGraph().getNode(0));
		exp.add( faster_connected.getGraph().getNode(1));
		exp.add( faster_connected.getGraph().getNode(2));
		exp.add( faster_connected.getGraph().getNode(5));
		exp.add( faster_connected.getGraph().getNode(4));
		ans = faster_connected.tsp(req);
		for(int i = 0; i<ans.size(); i++) {
			assertTrue(ans.get(i).getKey() == exp.get(i).getKey());
		}
	}
	
	@Test
	void test_center() {
		assertTrue(not_connected1.center() == null);
		assertTrue(not_connected2.center() == null);
		assertTrue(not_connected3.center() == null);
		assertTrue(not_connected4.center() == null);
		assertTrue(connected1.center().getKey() == 1);
		assertTrue(diamond.center().getKey() == 0);
		assertTrue(faster_connected.center().getKey() == 1);
		assertTrue(faster_not_connected.center() == null);
		
	}
	

}
