package cz.matocmir.tours.forwardpath;

import com.umotional.planningalgorithms.core.Dijkstra;
import com.umotional.planningalgorithms.core.ShortestPathAlgorithm;
import cz.matocmir.tours.model.*;
import org.apache.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CandidateFinder {
	private final Logger log = Logger.getLogger(CandidateFinder.class);
	private TourGraph graph;

	public CandidateFinder(TourGraph graph) {
		this.graph = graph;
	}

	public List<Candidate> forwardSearch(TourRequest request) throws IllegalArgumentException {
		TourNode start = graph.getNode(request.getStartNode());
		TourNode goal = graph.getNode(request.getGoalNode());

		if (start == null) {
			throw new IllegalArgumentException("Start or goal node not in graph");
		}

		if (goal == null) {
			goal = start;
		}

		Candidate startCan = new Candidate(new TreeNode(null, null, start), 0, 0);
		ForwardPathGoalChecker gc = new ForwardPathGoalChecker(request.getMinLength(), goal);
		ForwardPathLabel startLabel = new ForwardPathLabel(start.getId(), new int[] { 0 }, null, startCan);
		ForwardPathLabelFactory labelFactory;

		labelFactory = new ForwardPathLabelFactory(graph, start.getId(), request.getMaxLength());

		ShortestPathAlgorithm<ForwardPathLabel, ForwardPath> alg = new Dijkstra(labelFactory, startLabel, gc,
				new ForwardPathFactory());
		List<ForwardPath> results = alg.call();
		List<Candidate> unfiltered = results.stream().sorted(Comparator.comparingInt(p -> p.getCostVector()[0]))
				.map(r -> r.getLastLabelObjId().getCandidate()).collect(Collectors.toList());

		unfiltered.addAll(labelFactory.getBoundaryNodes());

		return unfiltered;
	}
}
