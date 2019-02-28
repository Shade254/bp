package cz.matocmir.tours.point2point;

import com.umotional.planningalgorithms.core.LabelFactory;
import cz.matocmir.tours.model.Candidate;
import cz.matocmir.tours.model.TourEdge;
import cz.matocmir.tours.model.TourGraph;
import cz.matocmir.tours.model.TreeNode;
import cz.matocmir.tours.utils.TourUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Point2PointLabelFactory implements LabelFactory<Point2PointLabel> {
	private static final double epsilon = 0.001;
	private TourGraph graph;
	private int startNode;
	private int goalNode;
	private double factor;
	private double strictness = 0.8;
	private int maxLength;
	private double pointsDistance;

	public Point2PointLabelFactory(TourGraph graph, int startNode, int goalNode, double factor, int maxLength) {
		this.graph = graph;
		this.startNode = startNode;
		this.goalNode = goalNode;
		this.factor = factor;
		this.maxLength = maxLength;
		pointsDistance = TourUtils.computeGreatCircleDistance(graph.getNode(startNode), graph.getNode(goalNode));
	}

	@Override
	public List<Point2PointLabel> successorsOf(Point2PointLabel current) {
		Candidate curCan = current.getCandidate();
		List<TourEdge> outEdges = graph.getOutEdges(curCan.correspNode.getNode().getId());
		List<Point2PointLabel> labels = new ArrayList<>();

		double tourL = current.getCandidate().length + TourUtils
				.computeGreatCircleDistance(graph.getNode(goalNode), curCan.correspNode.getNode());

		if (tourL >= maxLength + epsilon) {
			return Collections.emptyList();
		}

		for (TourEdge e : outEdges) {
			Candidate nxtCan = new Candidate(new TreeNode(e, curCan.correspNode, e.getTo()),
					curCan.weight + countWeight(e, curCan), curCan.length + e.getLengthInMeters());
			Point2PointLabel label = new Point2PointLabel(e.getToId(), new int[] { (int) nxtCan.weight }, current,
					nxtCan);
			labels.add(label);
		}

		return labels;
	}

	private double countWeight(TourEdge newEdge, Candidate candidate) {
		double distance = candidate.length + (newEdge.getLengthInMeters() / 2);

		List<TourEdge> backPath = candidate.correspNode.pathFromRoot().stream().map(TreeNode::getEdgeFromParent).filter(
				Objects::nonNull)
				.collect(Collectors.toList());
		Collections.reverse(backPath);

		double roundnessPenalty = 0;
		for (TourEdge e : backPath) {
			distance -= (e.getLengthInMeters() / 2);
			double penalty = newEdge.roundnessPenalty(e, Math.min(distance, (Math.PI*pointsDistance) - distance), strictness);
			roundnessPenalty += (penalty * e.getLengthInMeters() * newEdge.getLengthInMeters());
			distance -= (e.getLengthInMeters() / 2);
		}

		roundnessPenalty /= maxLength;
		roundnessPenalty *= (2 * factor);

		return (newEdge.getCost() + roundnessPenalty);
	}
}
