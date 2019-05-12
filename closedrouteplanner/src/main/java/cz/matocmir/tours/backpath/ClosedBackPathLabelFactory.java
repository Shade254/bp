package cz.matocmir.tours.backpath;

import com.umotional.planningalgorithms.core.LabelFactory;
import cz.matocmir.tours.model.*;
import cz.matocmir.tours.utils.TourUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClosedBackPathLabelFactory implements LabelFactory<BackPathLabel> {
	private static final Logger log = Logger.getLogger(ClosedBackPathLabelFactory.class);
	private static final double epsilon = 0.001;
	private TourGraph graph;
	private double forwardLength;
	private double maxLength;
	private TourNode startNode;
	private List<TourEdge> forwardPath;
	private double factor;
	private double strictness = 0.8;

	public ClosedBackPathLabelFactory(TourGraph graph, double forwardLength, double maxLength, TourNode goalNode,
			List<TourEdge> forwardPath, double factor, double strictness) {
		this.graph = graph;
		this.maxLength = maxLength;
		this.forwardLength = forwardLength;
		this.startNode = goalNode;
		this.forwardPath = forwardPath;
		this.factor = factor;
		this.strictness = strictness;
	}

	@Override
	public List<BackPathLabel> successorsOf(BackPathLabel current) {

		Candidate curCan = current.getCandidate();
		List<TourEdge> outEdges = graph.getOutEdges(curCan.correspNode.getNode().getId());
		List<BackPathLabel> labels = new ArrayList<>();

		double tourL = current.getCandidate().length + forwardLength + TourUtils
				.computeGreatCircleDistance(startNode, curCan.correspNode.getNode());

		if (tourL >= maxLength + epsilon) {
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

	private double countWeight(TourEdge e, double length) {
		double total_distance = forwardLength + length + (e.getLengthInMeters() / 2);
		double distance = total_distance;

		double roundnessPenalty = 0;
		for (TourEdge e1 : forwardPath) {
			distance -= (e1.getLengthInMeters() / 2);
			double penalty = e1.roundnessPenalty(e1, Math.min(distance, maxLength - distance), strictness);
			roundnessPenalty += (penalty * e1.getLengthInMeters() * e1.getLengthInMeters());
			distance -= (e1.getLengthInMeters() / 2);
		}

		roundnessPenalty /= maxLength;
		roundnessPenalty *= (2 * factor);

		return (e.getCost() + roundnessPenalty);
	}
}
