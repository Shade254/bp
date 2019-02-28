package cz.matocmir.tours.point2point;

import com.umotional.planningalgorithms.core.Dijkstra;
import com.umotional.planningalgorithms.core.ShortestPathAlgorithm;
import cz.matocmir.tours.model.Candidate;
import cz.matocmir.tours.model.TourEdge;
import cz.matocmir.tours.model.TourGraph;
import cz.matocmir.tours.model.TreeNode;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PointToPointFinder {
	private static final double epsilon = 0.001;

	private TourGraph graph;
	private int minLength;
	private int maxLength;
	private int startID;
	private int goalID;

	public PointToPointFinder(TourGraph graph, int minLength, int maxLength, int startID, int goalID) {
		this.graph = graph;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.startID = startID;
		this.goalID = goalID;
	}

	public List<List<TourEdge>> searchForPaths(int num) {
		Candidate startCan = new Candidate(new TreeNode(null, null, graph.getNode(startID)), 0, 0);
		Point2PointGoalChecker gc = new Point2PointGoalChecker(goalID);
		Point2PointLabelFactory labelFactory = new Point2PointLabelFactory(graph, startID, goalID, 10.2, maxLength);

		Point2PointLabel start = new Point2PointLabel(startID, new int[] { 0 }, null, startCan);
		ShortestPathAlgorithm<Point2PointLabel, Point2PointPath> alg = new Dijkstra(labelFactory, start, gc,
				new Point2PointPathFactory());

		List<Point2PointPath> results = alg.call();
		return results
				.stream()
				.filter(e -> e.getLastLabelObjId().getCandidate().length >= minLength + epsilon)
				.filter(e -> e.getLastLabelObjId().getCandidate().length <= maxLength + epsilon)
				.sorted(Comparator.comparingInt(p -> p.getCostVector()[0]))
				.limit(num)
				.map(Point2PointPath::getExactPath)
				.collect(Collectors.toList());
	}
}
