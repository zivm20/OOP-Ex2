package api_implementation;

import java.util.HashMap;

import api.EdgeData;


public class Edge implements EdgeData {
	private int src, dest, tag;
	private String info;
	private double weight;
	
	/**
	 * Copy constructor
	 * @param e - EdgeData object
	 */
	public Edge(EdgeData e) {
		this.src = e.getSrc();
		this.dest = e.getDest();
		this.weight = e.getWeight();
		this.info = e.getInfo();
		this.tag = e.getTag();
		
	}
	/**
	 * Constructor for Edge
	 * @param src - the index of the source node
	 * @param dest - the index of the destination node
	 * @param weight - weight of the edge
	 */
	public Edge(int src, int dest, double weight) {
		this.src = src;
		this.dest = dest;
		this.weight = weight;
		//no value
		this.info = "";
		this.tag = 0;
		
	}
	
	@Override
	public int getSrc() {
		return this.src;
	}

	@Override
	public int getDest() {
		return this.dest;
	}

	@Override
	public double getWeight() {
		return this.weight;
	}

	@Override
	public String getInfo() {
		return this.info;
	}

	@Override
	public void setInfo(String s) {
		this.info = s;

	}

	@Override
	public int getTag() {
		return this.tag;
	}

	@Override
	public void setTag(int t) {
		this.tag = t;

	}
	@Override
	public String toString() {
		HashMap<String,String> out = new HashMap<String,String>();
		out.put("src",this.src + "");
		out.put("w",this.weight+"");
		out.put("dest",this.dest+"");
		return out.toString();
	}

}
