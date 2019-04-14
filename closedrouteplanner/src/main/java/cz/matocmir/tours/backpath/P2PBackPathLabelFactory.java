package cz.matocmir.tours.backpath;

import com.umotional.planningalgorithms.core.LabelFactory;
import cz.matocmir.tours.model.*;
import cz.matocmir.tours.utils.TourUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class P2PBackPathLabelFactory implements LabelFactory<BackPathLabel> {
	private static final double epsilon = 0.001;
	private final double forwardLength;
	private final TourGraph graph;
	private final double maxLength;
	private final List<TourEdge> forwardPath;
	private final TourNode goalNode;
	private final double strictness;
	private final double factor;

	public P2PBackPathLabelFactory(TourGraph graph, List<TourEdge> forwardPath, double forwardLength,
			TourRequest request) {
		this.graph = graph;
		this.forwardLength = forwardLength;
		this.forwardPath = forwardPath;
		this.goalNode = graph.getNode(request.getGoalNode());
		this.maxLength = request.getMaxLength();
		this.strictness = request.getStrictness();
		this.factor = request.getFactor();
	}

	private double countWeight(TourEdge e, double length) {
		double total_distance = forwardLength + length + (e.getLengthInMeters() / 2);
		double distance = total_distance;

		double roundnessPenalty = 0;
		for (TourEdge e1 : forwardPath) {
			distance -= (e1.getLengthInMeters() / 2);
			double penalty = e.roundnessPenalty(e1, distance, strictness);
			roundnessPenalty += (penalty * e1.getLengthInMeters() * e.getLengthInMeters());
			distance -= (e1.getLengthInMeters() / 2);
		}

		roundnessPenalty /= maxLength;
		roundnessPenalty *= (2 * factor);
		return (e.getCost() + roundnessPenalty);
	}

	@Override
	public List<BackPathLabel> successorsOf(BackPathLabel current) {
		Candidate curCan = current.getCandidate();
		List<TourEdge> outEdges = graph.getOutEdges(curCan.correspNode.getNode().getId());
		List<BackPathLabel> labels = new ArrayList<>();

		double tourL = current.getCandidate().length + TourUtils
				.computeGreatCircleDistance(goalNode, curCan.correspNode.getNode());

		if (tourL + forwardLength >= maxLength + epsilon) {
			return Collections.emptyList();
		}

		for (TourEdge e : outEdges) {
			double weight = countWeight(e, curCan.length);
			Candidate nxtCan = new Candidate(new TreeNode(e, curCan.correspNode, e.getTo()), curCan.weight + weight,
					curCan.length + e.getLengthInMeters());
			BackPathLabel label = new BackPathLabel(e.getToId(), new int[] { (int) nxtCan.weight }, current, nxtCan);
			labels.add(label);
		}

		return labels;
	}
}
