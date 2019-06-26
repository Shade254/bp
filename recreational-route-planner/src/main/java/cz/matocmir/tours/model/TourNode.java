package cz.matocmir.tours.model;

import com.umotional.basestructures.GPSLocation;
import com.umotional.basestructures.INode;
import com.umotional.geotools.Transformer;
import com.vividsolutions.jts.geom.Coordinate;

import java.io.Serializable;

/***
 * Representation of single node in the graph
 */
public class TourNode implements INode, Serializable {
	public GPSLocation location;
	public int id;

	public TourNode() {
	}

	public TourNode(double lat, double lon, int id, int srid) {
		Transformer transformer = new Transformer(srid);
		Coordinate c = transformer.toProjected(new Coordinate(lat, lon));
		this.location = new GPSLocation(lat, lon, (int)c.x, (int)c.y);
		this.id = id;
	}


	public TourNode(double lat, double lon, int id, double latProjected, double lonProjected) {
		this.location = new GPSLocation(lat, lon, (int)latProjected, (int)lonProjected);
		this.id = id;
	}



	@Override
	public double getLatitude() {
		return location.getLatitude();
	}

	@Override
	public double getLongitude() {
		return location.getLongitude();
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getElevationInMillimeters() {
		return -1;
	}

	public double getLonProjected() {
		return location.lonProjectedE1;
	}

	public double getLatProjected() {
		return location.latProjectedE1;
	}
}
