package cz.matocmir.tours.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.umotional.basestructures.GPSLocation;
import com.umotional.basestructures.IEdge;
import com.umotional.basestructures.INode;
import cz.matocmir.tours.utils.TourUtils;

import java.util.List;

/***
 * Representation of single edge in the graph
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TourEdge implements IEdge {
	private TourNode from;
	private TourNode to;
	private double cost;
	private double length;
	private GPSLocation middle;

	public TourEdge() {
	}

	public TourEdge(TourNode from, TourNode to, double cost, double length) {
		this.from = from;
		this.to = to;
		this.cost = cost;
		this.length = length;
		middle = new GPSLocation((from.getLatitude() + to.getLatitude() / 2),
				(from.getLongitude() + to.getLongitude()) / 2, 0, 0);
	}

	public TourEdge(TourNode from, TourNode to, double cost) {
		this.from = from;
		this.to = to;
		this.cost = cost;
		length = (int) (TourUtils.computeGreatCircleDistance(from.getLatitude(), from.getLongitude(), to.getLatitude(),
				to.getLongitude()));
		middle = new GPSLocation((from.getLatitude() + to.getLatitude() / 2),
				(from.getLongitude() + to.getLongitude()) / 2, 0, 0);
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

		//System.out.println("Expected distance " + dExp);

		double displacement = TourUtils
				.computeGreatCircleDistance(middle.getLatitude(), middle.getLongitude(), e2.middle.getLatitude(),
						e2.middle.getLongitude());

		//System.out.println("Actual displacement " + displacement);

		if (displacement >= (dExp * strictness)) {
			return 0;
		}

		return ((strictness * dExp) - displacement) / (strictness * dExp);
	}

	public double getDisplacement(TourEdge e2) {
		return TourUtils
				.computeGreatCircleDistance(middle.getLatitude(), middle.getLongitude(), e2.middle.getLatitude(),
						e2.middle.getLongitude());
	}

	public TourNode getFrom() {
		return from;
	}

	public TourNode getTo() {
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

	@Override
	public String toString() {
		return "TourEdge{" + "from=" + from + ", to=" + to + ", cost=" + cost + ", length=" + length + ", middle="
				+ middle + '}';
	}
}
