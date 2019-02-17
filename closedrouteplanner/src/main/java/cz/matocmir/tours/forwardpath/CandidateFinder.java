package cz.matocmir.tours.forwardpath;

import com.umotional.planningalgorithms.core.Dijkstra;
import com.umotional.planningalgorithms.core.ShortestPathAlgorithm;
import cz.matocmir.tours.backpath.BackPathFactory;
import cz.matocmir.tours.model.*;
import cz.matocmir.tours.utils.TourUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class CandidateFinder {
	private final Logger log = Logger.getLogger(CandidateFinder.class);
	private final double epsilon = 0.001;
	private TourGraph graph;

	public CandidateFinder(TourGraph graph) {
		this.graph = graph;
	}

	public List<Candidate> forwardSearch(TourNode startNode, int minLength, int maxLength) {
		Candidate startCan = new Candidate(new TreeNode(null, null, startNode), 0, 0);
		ForwardPathGoalChecker gc = new ForwardPathGoalChecker(minLength, startNode);
		ForwardPathLabelFactory labelFactory = new ForwardPathLabelFactory(graph, maxLength, startNode);
		ForwardPathLabel start = new ForwardPathLabel(startNode.getId(), new int[] { 0 }, null, startCan);

		ShortestPathAlgorithm<ForwardPathLabel, ForwardPath> alg = new Dijkstra(labelFactory, start, gc,
				new ForwardPathFactory());
		List<ForwardPath> results = alg.call();
		List<Candidate> candidates = results.stream().map(p -> p.getLastLabelObjId().getCandidate())
				.collect(Collectors.toList());
		candidates.addAll(labelFactory.getBoundaryNodes());

		return candidates;
	}
}
