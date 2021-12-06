package api_implementation;

import api.GeoLocation;
import api.NodeData;

public class Node implements NodeData {
	private GeoLocation point;
	private int key;
	private double weight;
	private String info;
	private int tag;
	
	/**
	 * Copy constructor
	 * @param n - NodeData object
	 */
	public Node(NodeData n) {
		this.point = n.getLocation();
		this.key = n.getKey();
		this.weight = n.getWeight();
		this.info = n.getInfo();
		this.tag = n.getTag();
		
	}
	/**
	 * Constructor for Node 
	 * @param key - key of the Node
	 * @param point - location of the Node
	 */
	public Node(int key, GeoLocation point) {
		this.key = key;
		this.point = new Point(point);
		//no value
		this.weight = 0;
		this.info = "";
		this.tag = 0;
		
	}
	
	
	
	@Override
	public int getKey() {
		return this.key;
	}

	@Override
	public GeoLocation getLocation() {
		return this.point;
	}

	@Override
	public void setLocation(GeoLocation p) {
		this.point = new Point(p);
	}
	@Override
	public double getWeight() {
		return this.weight;
	}

	@Override
	public void setWeight(double w) {
		this.weight = w;
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

}
