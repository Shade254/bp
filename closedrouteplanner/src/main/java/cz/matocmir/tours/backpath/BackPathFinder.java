package cz.matocmir.tours.backpath;

import com.umotional.planningalgorithms.core.Dijkstra;
import com.umotional.planningalgorithms.core.LabelFactory;
import com.umotional.planningalgorithms.core.ShortestPathAlgorithm;
import cz.matocmir.tours.model.*;
import cz.matocmir.tours.utils.TourUtils;
import javafx.util.Pair;
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

	public ArrayList<TourEdge> getCompletedPath(Candidate candidate, TourRequest request) {
		TourNode startingNode = graph.getNode(request.getStartNode());
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
					< request.getMinLength() / 2 * beta) {
				continue;
			}

			BackPath bp = getPathsWithRoundness(request, forwardPath.get(i).getNode(), forwardLength,
					partialForwardPath);

			if (bp == null)
				continue;

			if ((forwardLength + bp.getTotalLength()) <= request.getMaxLength() + epsilon
					&& (forwardLength + bp.getTotalLength()) >= request.getMinLength() + epsilon) {
				if (forwardCost + bp.getCostVector()[0] < bestScore) {
					bestScore = forwardCost + bp.getCostVector()[0];
					bestPath = new ArrayList<>();
					bestPath.addAll(partialForwardPath);
					List<TourEdge> backPath = bp.getExactPath();
					bestPath.addAll(backPath);
				}
			}
		}

		return bestPath;
	}

	private BackPath getPathsWithRoundness(TourRequest request, TourNode start, double forwardLength,
			List<TourEdge> forwardPath) {
		TourNode st = graph.getNode(request.getStartNode());
		TourNode go = graph.getNode(request.getGoalNode());
		boolean p2p = true;
		if (st == null) {
			throw new IllegalArgumentException("Start not found");
		}

		if (go == null) {
			go = st;
			p2p = false;
		}

		Candidate startCan = new Candidate(new TreeNode(null, null, start), 0, 0);
		BackPathGoalChecker gc = new BackPathGoalChecker(go);
		BackPathLabel startLabel = new BackPathLabel(start.getId(), new int[] { 0 }, null, startCan);
		LabelFactory<BackPathLabel> labelFactory;

		if (p2p) {
			labelFactory = new P2PBackPathLabelFactory(graph, forwardPath, forwardLength, request);
		} else {
			labelFactory = new ClosedBackPathLabelFactory(graph, forwardLength, request.getMaxLength(), go, forwardPath,
					request.getFactor(), request.getStrictness());
		}

		ShortestPathAlgorithm<BackPathLabel, BackPath> alg = new Dijkstra(labelFactory, startLabel, gc,
				new BackPathFactory());
		List<BackPath> res = alg.call();
		if (res == null || res.isEmpty())
			return null;

		return res.get(0);
	}

	public Pair<TourNode, List<TourEdge>> getBestPathBack(Candidate lastTurningPoint, TourRequest request) {
		ArrayList<TreeNode> forwardPath = lastTurningPoint.correspNode.pathFromRoot();
		ArrayList<TourEdge> partialForwardPath = new ArrayList<>();

		List<TourEdge> bestPath = null;
		double bestScore = Double.MAX_VALUE;
		TourNode bestTurningPoint = null;

		double forwardLength = 0;
		double forwardCost = 0;

		for (int i = 1; i < forwardPath.size(); i++) {
			TourEdge nextEdge = forwardPath.get(i).getEdgeFromParent();

			partialForwardPath.add(nextEdge);
			forwardLength += nextEdge.getLengthInMeters();
			forwardCost += nextEdge.getCost();

			TourNode turnP = forwardPath.get(i).getNode();

			if (TourUtils.computeGreatCircleDistance(graph.getNode(request.getStartNode()), turnP)
					< request.getMinLength() / 2 * beta) {
				continue;
			}

			BackPath pathBack = this.getPathsWithRoundness(request, turnP, forwardLength, partialForwardPath);

			if (pathBack != null) {
				Candidate last = pathBack.getLastLabelObjId().getCandidate();
				if (last.weight + forwardCost < bestScore) {
					bestTurningPoint = turnP;
					bestScore = last.weight + forwardCost;

					List<TreeNode> oneTour = new ArrayList<>(forwardPath.get(i).pathFromRoot());
					oneTour.addAll(last.correspNode.pathFromRoot());

					bestPath = oneTour.stream().map(TreeNode::getEdgeFromParent).filter(Objects::nonNull)
							.collect(Collectors.toList());
				}

			}
		}

		return new Pair<>(lastTurningPoint.correspNode.getNode(), bestPath);
	}

}
