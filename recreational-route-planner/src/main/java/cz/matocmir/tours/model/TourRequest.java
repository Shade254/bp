package cz.matocmir.tours.model;

/***
 * Request to plan a tour. Individual parameters are described in the paper Chapter 3
 */
public class TourRequest {
	private static final double DEFAULT_STRICTNESS = 0.8;
	private static final double DEFAULT_FACTOR = 32;
	private int startNode;
	private int goalNode;
	private double factor;
	private double strictness;
	private double minLength;
	private double maxLength;

	public TourRequest() {
		startNode = -1;
		goalNode = -1;
	}

	public TourRequest(int startNode, int goalNode, double factor, double strictness, double minLength,
			double maxLength) {
		this.startNode = startNode;
		this.goalNode = goalNode;
		this.factor = factor;
		this.strictness = strictness;
		this.minLength = minLength;
		this.maxLength = maxLength;

		if (strictness < 0)
			this.setStrictness(DEFAULT_STRICTNESS);
		if (factor < 0)
			this.setFactor(DEFAULT_FACTOR);
	}

	public int getStartNode() {
		return startNode;
	}

	public void setStartNode(int startNode) {
		this.startNode = startNode;
	}

	public int getGoalNode() {
		return goalNode;
	}

	public void setGoalNode(int goalNode) {
		this.goalNode = goalNode;
	}

	public double getFactor() {
		return factor;
	}

	public void setFactor(double factor) {
		this.factor = factor;
	}

	public double getStrictness() {
		return strictness;
	}

	public void setStrictness(double strictness) {
		this.strictness = strictness;
	}

	public double getMinLength() {
		return minLength;
	}

	public void setMinLength(double minLength) {
		this.minLength = minLength;
	}

	public double getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(double maxLength) {
		this.maxLength = maxLength;
	}

	public boolean isClosed() {
		return startNode != -1 && goalNode == -1;
	}

	@Override
	public String toString() {
		return "TourRequest{" + "startNode=" + startNode + ", goalNode=" + goalNode + ", factor=" + factor
				+ ", strictness=" + strictness + ", minLength=" + minLength + ", maxLength=" + maxLength + '}';
	}
}
