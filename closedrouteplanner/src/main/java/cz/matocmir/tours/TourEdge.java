package cz.matocmir.tours;

import com.umotional.basestructures.GPSLocation;
import com.umotional.basestructures.IEdge;
import com.umotional.basestructures.INode;

import java.util.List;

public class TourEdge implements IEdge {
	private TourNode from;
	private TourNode to;
	private int cost;
	private int length;
	private GPSLocation middle;

	public TourEdge(TourNode from, TourNode to, int cost) {
		this.from = from;
		this.to = to;
		this.cost = cost;
		length = (int)(TourUtils.computeEuclideanDistance(from.getLatitude(), from.getLongitude(), to.getLatitude(), to.getLongitude())/10);
		//TODO - fix, edges in osm graph are not straight only
		middle = new GPSLocation((from.getLatitude()+to.getLatitude()/2), (from.getLongitude()+to.getLongitude())/2, 0, 0);
	}

	public TourNode getFrom() {
		return from;
	}

	public TourNode getTo() {
		return to;
	}

	public int getCost() {
		return cost;
	}

	public GPSLocation getMiddle() {
		return middle;
	}

	@Override
	public int getFromId() {
		return from.getId();
	}

	@Override
	public int getToId() {
		return to.getId();
	}

	@Override
	public double getLengthInMeters() {
		return length;
	}

	@Override
	public List<? extends INode> getViaNodes() {
		return null;
	}
}
