package cz.matocmir.tours.api;

import com.umotional.basestructures.BoundingBox;
import com.umotional.geotools.KDTree;
import cz.matocmir.tours.backpath.BackPathFinder;
import cz.matocmir.tours.filters.CandidateFilter;
import cz.matocmir.tours.filters.DeadEndFilter;
import cz.matocmir.tours.forwardpath.CandidateFinder;
import cz.matocmir.tours.forwardpath.CandidatesPicker;
import cz.matocmir.tours.model.*;
import cz.matocmir.tours.utils.TourNodeResolver;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlannerService {
	private static final String PATH_TO_GRAPH = "./src/main/resources/prague_min.csv";
	private static final Logger log = Logger.getLogger(PlannerService.class);
	private static final int DEFAULT_TOURS_NUM = 5;

	private static TourGraph graph = TourGraph.graphFromCSV(PATH_TO_GRAPH);
	private CandidateFinder candFinder;
	private BackPathFinder backPathFinder;
	private BoundingBox graphBorders;
	private KDTree<TourNode> nodesTree;

	PlannerService() {
		log.info("Graph loaded");
		nodesTree = new KDTree<>(2, new TourNodeResolver<>());
		//graph.graphToGeojson("all_cycle.geojson");
		candFinder = new CandidateFinder(graph);
		backPathFinder = new BackPathFinder(graph);
		List<Double> longs = graph.getAllNodes().stream().map(TourNode::getLongitude).sorted()
				.collect(Collectors.toList());
		List<Double> lats = graph.getAllNodes().stream().map(TourNode::getLatitude).sorted()
				.collect(Collectors.toList());
		graphBorders = new BoundingBox(lats.get(0), longs.get(0), lats.get(lats.size() - 1),
				longs.get(longs.size() - 1));
		graph.getAllNodes().stream().forEach(nodesTree::insert);
	}

	public TourNode getNearestNode(double[] coords) {
		return nodesTree.getNearestNode(coords);
	}

	public BoundingBox getGraphBorders() {
		return graphBorders;
	}

	public TourResponse getClosedTours(TourRequest request, int toursNumber) throws IllegalArgumentException {
		log.info("Starting CLOSED for request " + request.toString());
		long stopwatch = System.currentTimeMillis();
		TourResponse response = new TourResponse();

		List<Candidate> cands = candFinder.forwardSearch(request);

		CandidateFilter candidateFilter = new DeadEndFilter(graph);
		cands = candidateFilter.filter(cands);

		if (cands.isEmpty())
			return null;

		log.info("Found " + cands.size() + " candidates");

		TourNode startingNode = graph.getNode(request.getStartNode());
		CandidatesPicker candidatesPicker = new CandidatesPicker(cands, startingNode.getLongitude(),
				startingNode.getLatitude(), request.getMinLength());

		if (toursNumber < 0)
			toursNumber = DEFAULT_TOURS_NUM;

		int foundCycles = 0;
		List<Tour> paths = new ArrayList<>();
		Tour foundPath;

		for (int i = 0; i < cands.size(); i++) {
			Candidate randomCand = candidatesPicker.selectCandidate();

			if (randomCand == null || randomCand.correspNode == null)
				continue;

			foundPath = backPathFinder.getCompletedPath(randomCand, request);

			if (foundPath != null && foundPath.getOriginalEdges() != null && !foundPath.getOriginalEdges().isEmpty()) {
				System.out.println(paths.size() + ". path to goal found");
				paths.add(foundPath);
				foundCycles++;
			}

			if (foundCycles >= toursNumber) {
				log.info("All cycles found");
				break;
			}
		}

		response.setResponseTime(System.currentTimeMillis() - stopwatch);
		response.setTours(paths.toArray(new Tour[0]));

		return response;
	}

	public TourResponse getP2PTours(TourRequest request, int toursNumber) throws IllegalArgumentException {
		log.info("Starting P2P for request " + request.toString());
		long stopwatch = System.currentTimeMillis();
		TourResponse response = new TourResponse();

		List<Tour> result = new ArrayList<>();

		TourNode startNode = graph.getNode(request.getStartNode());
		if (startNode == null)
			throw new IllegalArgumentException("Missing start node");

		TourNode goalNode = graph.getNode(request.getGoalNode());
		if (goalNode == null)
			throw new IllegalArgumentException("Missing goal node");

		List<Candidate> turningPoints = candFinder.forwardSearch(request);
		System.out.println("Found " + turningPoints.size() + " candidates");

		if (toursNumber < 0)
			toursNumber = DEFAULT_TOURS_NUM;

		double lat = startNode.getLatitude() + goalNode.getLatitude();
		lat /= 2;

		double lon = startNode.getLongitude() + goalNode.getLongitude();
		lon /= 2;

		int foundWalks = 1;
		CandidatesPicker picker = new CandidatesPicker(turningPoints, lon, lat, request.getMaxLength());
		for (int i = 1; i < turningPoints.size(); i++) {
			Candidate turningPoint = picker.selectCandidate();
			if (turningPoint == null || turningPoint.correspNode == null) {
				continue;
			}

			Tour tour = backPathFinder.getBestPathBack(turningPoint, request);
			if (tour != null && tour.getOriginalEdges() != null && !tour.getOriginalEdges().isEmpty()) {
				System.out.println(foundWalks + ". path to goal found");
				result.add(tour);
				foundWalks++;
			}

			if (foundWalks >= toursNumber) {
				log.info("All cycles found");
				break;
			}

		}

		response.setResponseTime(System.currentTimeMillis() - stopwatch);
		response.setTours(result.toArray(new Tour[0]));

		return response;
	}

}
