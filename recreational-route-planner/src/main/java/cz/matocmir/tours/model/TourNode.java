package cz.matocmir.tours.model;

import com.umotional.basestructures.INode;
/***
 * Representation of single node in the graph
 */
public class TourNode implements INode {
	private double lat;
	private double lon;
	private int id;

	public TourNode() {
	}

	public TourNode(double lat, double lon, int id) {
		this.lat = lat;
		this.lon = lon;
		this.id = id;
	}

	@Override
	public double getLatitude() {
		return lat;
	}

	@Override
	public double getLongitude() {
		return lon;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getElevationInMillimeters() {
		return -1;
	}

	@Override
	public String toString() {
		return "TourNode{" + "lat=" + lat + ", lon=" + lon + ", id=" + id + '}';
	}
}
