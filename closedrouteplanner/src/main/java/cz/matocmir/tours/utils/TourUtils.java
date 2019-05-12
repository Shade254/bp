package cz.matocmir.tours.utils;

import cz.matocmir.tours.backpath.BackPathFinder;
import cz.matocmir.tours.filters.CandidateFilter;
import cz.matocmir.tours.filters.DeadEndFilter;
import cz.matocmir.tours.filters.NoEffectFilter;
import cz.matocmir.tours.forwardpath.CandidateFinder;
import cz.matocmir.tours.forwardpath.CandidatesPicker;
import cz.matocmir.tours.model.*;

import java.util.List;

public class TourUtils {

	public static void main(String[] args) {
		TourGraph g = TourGraph.graphFromCSV("./src/main/resources/cycle_prague.csv");
		CandidateFilter filter1 = new DeadEndFilter(g);
		CandidateFilter filter2 = new NoEffectFilter();
		double first, second;

		TourRequest request = new TourRequest();
		request.setStartNode(17594);
		request.setStrictness(1);
		request.setMinLength(6000);
		request.setMaxLength(7500);
		request.setFactor(1000);

		first = TourUtils.averagyPenaltyStats(request, g, filter1, 100);
		second = TourUtils.averagyPenaltyStats(request, g, filter2, 100);
		System.out.println("Filtered " + first + " unfiltered " + second);

		request.setStartNode(22597);
		first = TourUtils.averagyPenaltyStats(request, g, filter1, 100);
		second = TourUtils.averagyPenaltyStats(request, g, filter2, 100);
		System.out.println("Filtered " + first + " unfiltered " + second);

		request.setStartNode(23305);
		first = TourUtils.averagyPenaltyStats(request, g, filter1, 100);
		second = TourUtils.averagyPenaltyStats(request, g, filter2, 100);
		System.out.println("Filtered " + first + " unfiltered " + second);

		request.setStartNode(19970);
		first = TourUtils.averagyPenaltyStats(request, g, filter1, 100);
		second = TourUtils.averagyPenaltyStats(request, g, filter2, 100);
		System.out.println("Filtered " + first + " unfiltered " + second);

		request.setStartNode(20657);
		first = TourUtils.averagyPenaltyStats(request, g, filter1, 100);
		second = TourUtils.averagyPenaltyStats(request, g, filter2, 100);
		System.out.println("Filtered " + first + " unfiltered " + second);

	}

	public static double computeEuclideanDistance(double fromX, double fromY, double toX, double toY) {
		return Math.sqrt((fromX - toX) * (fromX - toX) + (fromY - toY) * (fromY - toY));
	}

	public static double computeGreatCircleDistance(double fromLat, double fromLon, double toLat, double toLon) {
		double earthRadius = 6371000.0D;
		double dLat = Math.toRadians(toLat - fromLat);
		double dLng = Math.toRadians(toLon - fromLon);
		double a = Math.sin(dLat / 2.0D) * Math.sin(dLat / 2.0D) + Math.cos(Math.toRadians(fromLat)) * Math
				.cos(Math.toRadians(toLat)) * Math.sin(dLng / 2.0D) * Math.sin(dLng / 2.0D);
		double c = 2.0D * Math.atan2(Math.sqrt(a), Math.sqrt(1.0D - a));
		float dist = (float) (earthRadius * c);
		return (double) dist;
	}

	public static double computeEuclideanDistance(TourNode node, TourNode node1) {
		return computeEuclideanDistance(node.getLatitude(), node.getLongitude(), node.getLatitude(),
				node.getLongitude());
	}

	//angle = 360/(2*pi*r/distance)
	//chord_length = 2*r*sin(angle)
	//we approximate by picewise linear function f, such that (l is circumference):
	//f(0)=0, f(l/2) = l/pi (half-circle), f(l) = 0
	//we can see that this function is symetrical by l/pi => f(x)=f(l-x)
	//than mean we can further approximate by linear function, which will work if we will use shorter from two distances only
	//this linear function g if therefore: g(x) = ((l/pi) / (l/2)) * x = 2*l*x/pi*l = 2*x/pi
	//this linear function is independent of circumference
	public static double getExpectedDisplacement(double distanceByCircumference) {
		return 2 * distanceByCircumference / Math.PI;
	}

	public static double computeGreatCircleDistance(TourNode node, TourNode node1) {
		return computeGreatCircleDistance(node.getLatitude(), node.getLongitude(), node1.getLatitude(),
				node1.getLongitude());
	}

	public static double averagyPenaltyStats(TourRequest request, TourGraph graph, CandidateFilter f, int sampleSize) {
		TourNode startNode = graph.getNode(request.getStartNode());
		if (startNode == null) {
			throw new IllegalArgumentException("Start node not found");
		}

		CandidateFinder finder = new CandidateFinder(graph);
		List<Candidate> candidates = finder.forwardSearch(request);

		CandidatesPicker picker;
		double cummulativePenalty = 0;
		picker = new CandidatesPicker(f.filter(candidates), startNode.getLongitude(), startNode.getLatitude(),
				request.getMinLength());
		int foundCycles = 0;
		for (int i = 0; i < candidates.size(); i++) {
			if (foundCycles >= sampleSize) {
				break;
			}

			Candidate c = picker.selectCandidate();

			if (c == null) {
				continue;
			}

			BackPathFinder backFinder = new BackPathFinder(graph);
			Tour pathBack = backFinder.completeClosedTourFromForwardPath(c, request);
			if (pathBack == null || pathBack.getOriginalEdges() == null || pathBack.getOriginalEdges().isEmpty()) {
				continue;
			}

			double penalty = tourPenalty(pathBack.getOriginalEdges(), getPathLength(pathBack.getOriginalEdges()));
			//System.out.println("Penalty for candidate " + c.correspNode.getNode() + "\nis " + penalty);
			cummulativePenalty += penalty;
			foundCycles++;
		}
		return (cummulativePenalty / foundCycles);
	}

	public static double getPathLength(List<TourEdge> path) {
		double l = 0;
		for (TourEdge e : path) {
			l += e.getLengthInMeters();
		}
		return l;
	}

	public static double tourPenalty(List<TourEdge> walk, double totalLength) {
		double penalty = 0;
		double distance;

		for (int i = 0; i < walk.size(); i++) {
			TourEdge curEdge = walk.get(i);
			distance = (curEdge.getLengthInMeters()/2);
			for (int j = i; j < walk.size(); j++) {
				TourEdge secondEdge = walk.get(j);
				distance += (secondEdge.getLengthInMeters()/2);
				penalty += 2 * (curEdge.roundnessPenalty(secondEdge, distance, 1) * curEdge.getLengthInMeters()
						* secondEdge.getLengthInMeters());
				distance += (secondEdge.getLengthInMeters()/2);

			}
		}
		penalty /= (Math.pow(totalLength, 2));
		return penalty;
	}

	public double getDegreeBetweenEdges(TourNode common, TourNode first, TourNode second) {
		double result =
				Math.atan2(first.getLatitude() - common.getLatitude(), first.getLongitude() - common.getLongitude())
						- Math.atan2(second.getLatitude() - common.getLatitude(),
						second.getLongitude() - common.getLongitude());

		if (result < 0) {
			result += (2 * Math.PI);
		}

		return result;
	}

}
