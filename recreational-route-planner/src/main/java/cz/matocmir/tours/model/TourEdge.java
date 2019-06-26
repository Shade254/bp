package cz.matocmir.tours.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.umotional.basestructures.GPSLocation;
import com.umotional.basestructures.IEdge;
import com.umotional.basestructures.INode;
import cz.matocmir.tours.utils.TourUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/***
 * Representation of single edge in the graph
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TourEdge implements IEdge, Serializable {
	public double cost;
	public double length;
	public GPSLocation middle;
	public int fromId;
	public int toId;
	public GPSLocation from;
	public GPSLocation to;

	public TourEdge() {
	}

	public TourEdge(TourNode from, TourNode to, double cost, double length) {
		this.fromId = from.getId();
		this.toId = to.getId();
		this.cost = cost;
		this.length = length;
		this.from = new GPSLocation(from.getLatitude(), from.getLongitude(), (int) from.getLatProjected(), (int) from.getLonProjected());
		this.to = new GPSLocation(to.getLatitude(), to.getLongitude(), (int) to.getLatProjected(), (int) to.getLonProjected());
		this.middle = createMiddle();
	}

	public TourEdge(TourNode from, TourNode to, double cost) {
		this.fromId = from.getId();
		this.toId = to.getId();
		this.cost = cost;
		length = (int) (TourUtils.computeEuclideanDistance(from, to));
		this.from = new GPSLocation(from.getLatitude(), from.getLongitude(), (int) from.getLatProjected(), (int) from.getLonProjected());
		this.to = new GPSLocation(to.getLatitude(), to.getLongitude(), (int) to.getLatProjected(), (int) to.getLonProjected());
		this.middle = createMiddle();
	}

	/***
	 * Method to get roundness penalty of two edges
	 * @param e2 Second edge against which the penalty is counted
	 * @param distance Distance by circumference of the tour
	 * @param strictness Sigma parameter, specified in paper Chapter 4
	 * @return value of the roundness penalty
	 */
	public double roundnessPenalty(TourEdge e2, double distance, double strictness) {
		double dExp = TourUtils.getExpectedDisplacement(distance);
		double displacement = TourUtils
				.computeEuclideanDistance(this.middle.latProjectedE1, this.middle.lonProjectedE1, e2.middle.latProjectedE1, e2.middle.lonProjectedE1);

		System.out.println("Expected displacement " + dExp);
		System.out.println("Actual displacement " + displacement);

		if (displacement >= (dExp * strictness)) {
			return 0;
		}

		return ((strictness * dExp) - displacement) / (strictness * dExp);
	}

	public GPSLocation getFrom() {
		return from;
	}

	public GPSLocation getTo() {
		return to;
	}

	public double getCost() {
		return cost;
	}

	public GPSLocation getMiddle() {
		return middle;
	}

	@Override
	public int getFromId() {
		return fromId;
	}

	@Override
	public int getToId() {
		return toId;
	}

	@Override
	public double getLengthInMeters() {
		return length;
	}

	@Override
	public List<? extends INode> getViaNodes() {
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TourEdge tourEdge = (TourEdge) o;
		return Double.compare(tourEdge.getCost(), getCost()) == 0 && Double.compare(tourEdge.length, length) == 0
				&& Objects.equals(getFrom(), tourEdge.getFrom()) && Objects.equals(getTo(), tourEdge.getTo()) && Objects
				.equals(getMiddle(), tourEdge.getMiddle());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getFrom(), getTo(), getCost(), length, getMiddle());
	}

	@Override
	public String toString() {
		return "TourEdge{" + "from=" + from + ", to=" + to + ", cost=" + cost + ", length=" + length + ", middle="
				+ middle + '}';
	}

	private GPSLocation createMiddle(){
		return new GPSLocation((from.getLatitude() + to.getLatitude())/2,
		(from.getLongitude() + to.getLongitude()) / 2, (from.latProjectedE1+to.latProjectedE1)/2, (from.lonProjectedE1+to.lonProjectedE1)/2);
	}
}
