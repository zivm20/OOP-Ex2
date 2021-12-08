package api_implementation;

import api.GeoLocation;

public class Point implements GeoLocation {
	private double x,y,z;
	/**
	 * Constructor
	 * @param x - x value of point
	 * @param y - y value of point
	 * @param z - z value of point
	 */
	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	/**
	 * Copy constructor
	 * @param p - GeoLocation to copy 
	 */
	public Point(GeoLocation p) {
		this.x = p.x();
		this.y = p.y();
		this.z = p.z();
	}
	@Override
	public double x() {
		return this.x;
	}

	@Override
	public double y() {
		return this.y;
	}

	@Override
	public double z() {
		return this.y;
	}
	@Override
	public double distance(GeoLocation g) {
		double distance = Math.pow(this.x - g.x(), 2) + Math.pow(this.y - g.y(), 2) + Math.pow(this.z - g.z(), 2);
		return Math.sqrt(distance);
	}
	public String toString() {
		return this.x+","+this.y+","+this.z;
	}

}
