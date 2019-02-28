package cz.matocmir.tours.backpath;

import com.umotional.planningalgorithms.core.Dijkstra;
import com.umotional.planningalgorithms.core.ShortestPathAlgorithm;
import cz.matocmir.tours.model.*;
import cz.matocmir.tours.utils.TourUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BackPathFinder {
	private static final Logger log = Logger.getLogger(BackPathFinder.class);
	private static double epsilon = 0.001;
	private static double beta = 0.8;

	public TourGraph graph;

	public BackPathFinder(TourGraph graph) {
		this.graph = graph;
	}

	public ArrayList<TourEdge> getPathBack(Candidate candidate, TourNode startingNode, double maxLength,
			double minLength, double factor, double strictness) {
		ArrayList<TreeNode> forwardPath = candidate.correspNode.pathFromRoot();

		ArrayList<TourEdge> partialForwardPath = new ArrayList<>();

		ArrayList<TourEdge> bestPath = null;
		double bestScore = Double.MAX_VALUE;

		double forwardLength = 0;
		double forwardCost = 0;

		for (int i = 1; i < forwardPath.size(); i++) {
			TourEdge nextEdge = forwardPath.get(i).getEdgeFromParent();

			partialForwardPath.add(nextEdge);
			forwardLength += nextEdge.getLengthInMeters();
			forwardCost += nextEdge.getCost();

			if (TourUtils.computeGreatCircleDistance(startingNode, forwardPath.get(i).getNode())
					< minLength / 2 * beta) {
				continue;
			}

			Candidate startCan = new Candidate(new TreeNode(null, null, forwardPath.get(i).getNode()), 0, 0);

			BackPathGoalChecker gc = new BackPathGoalChecker(startingNode);
			BackPathLabelFactory lf = new BackPathLabelFactory(graph, forwardLength, maxLength, startingNode,
					forwardPath.stream().map(TreeNode::getEdgeFromParent).filter(Objects::nonNull)
							.collect(Collectors.toList()), factor, strictness);

			BackPathLabel start = new BackPathLabel(forwardPath.get(i).getNode().getId(), new int[] { 0 }, null,
					startCan);

			ShortestPathAlgorithm<BackPathLabel, BackPath> alg = new Dijkstra(lf, start, gc, new BackPathFactory());

			List<BackPath> res = alg.call();
			BackPath bp;
			if (res == null || res.isEmpty()) {
				continue;
			} else {
				bp = res.get(0);
			}

			if ((forwardLength + bp.getTotalLength()) <= maxLength + epsilon
					&& (forwardLength + bp.getTotalLength()) >= minLength + epsilon) {
				if (forwardCost + bp.getCostVector()[0] < bestScore) {
					bestScore = forwardCost + bp.getCostVector()[0];
					bestPath = new ArrayList<>();
					bestPath.addAll(partialForwardPath);
					List<TourEdge> backPath = bp.getFullPath().stream().map(TreeNode::getEdgeFromParent)
							.filter(Objects::nonNull).collect(Collectors.toList());
					bestPath.addAll(backPath);
				}
			}
		}

		return bestPath;
	}
}
