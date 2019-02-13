package cz.matocmir.tours;

import com.umotional.basestructures.INode;

public class TourNode implements INode {
	private double lat;
	private double lon;
	private int id;

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
}
