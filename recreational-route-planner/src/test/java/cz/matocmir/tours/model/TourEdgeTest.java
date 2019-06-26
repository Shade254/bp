package cz.matocmir.tours.model;

import cz.matocmir.tours.utils.TourUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TourEdgeTest {
	private static final String PATH_TO_GRAPH = "./src/main/resources/prague_min.csv";
	private static final int SRID = 2065;


	// Testing correct evaluation of edge roundness penalty
	@Test
	public void edgeRoundnessPenaltyTest() {
		// nodes in shape of a rectangle
		TourNode n1 = new TourNode(50.082390, 14.504781, 1, 2065);
		TourNode n2 = new TourNode(50.081282, 14.505167, 2, 2065);
		TourNode n3 = new TourNode(50.081013, 14.503171, 3, 2065);
		TourNode n4 = new TourNode(50.082087, 14.502763, 4, 2065);

		//nodes further away but parallel with first edge
		TourNode s1 = new TourNode(50.081970, 14.501690, 5, 2065);
		TourNode s2 = new TourNode(50.080930, 14.502087, 6, 2065);


		TourEdge e1 = new TourEdge(n1, n2, 1.1);
		TourEdge e2 = new TourEdge(n2, n3, 1.1);
		TourEdge e3 = new TourEdge(n2, n1, 1.1);
		TourEdge e4 = new TourEdge(n3, n4, 1.1);
		TourEdge e5 = new TourEdge(s2, s1, 1.1);

		// penalty of right angle, should be close to 0
		double right = e1.roundnessPenalty(e2, e1.getLengthInMeters()/2 + e2.getLengthInMeters()/2,1);
		System.out.println(right);

		// further edge should have greater penalty
		double near = e1.roundnessPenalty(e4, e1.getLengthInMeters()/2 + e4.getLengthInMeters()/2 + 100,1);
		System.out.println(near);

		double far = e1.roundnessPenalty(e5, e1.getLengthInMeters()/2 + e5.getLengthInMeters()/2 + 100,1);
		System.out.println(far);

		// max penalty
		double parallel = e1.roundnessPenalty(e3, e1.getLengthInMeters()/2 + e3.getLengthInMeters()/2, 1);
		System.out.println(parallel);

		assertTrue(right <= near);
		assertTrue(right <= far);

		assertTrue(far < parallel);

		assertTrue(near < parallel);
	}


	// Testing the roundness penalty of identical forward and backward path
	@Test
	public void samePathRoundnessTest() {
		TourGraph g = TourGraph.graphFromCSV(PATH_TO_GRAPH, SRID);
		List<TourEdge> firstTrack = new ArrayList<>();
		firstTrack.add(g.getEdge(141, 2105));
//		firstTrack.add(g.getEdge(2105, 2090));
//		firstTrack.add(g.getEdge(2090, 2734));
//		firstTrack.add(g.getEdge(2734, 2094));
//		firstTrack.add(g.getEdge(2094, 2739));
//		firstTrack.add(g.getEdge(2739, 2754));
//		firstTrack.add(g.getEdge(2754, 2756));
//		firstTrack.add(g.getEdge(2756, 2015));
//		firstTrack.add(g.getEdge(2015, 2729));
//		firstTrack.add(g.getEdge(2729, 2757));
//		firstTrack.add(g.getEdge(2757, 2413));

		int orig = firstTrack.size();
		List<TourEdge> backEdges = new ArrayList<>();
		for(int i = 0;i<orig;i++){
			TourEdge there = firstTrack.get(firstTrack.size()-1-i);
			TourEdge here = g.getEdge(there.getToId(), there.getFromId());
			backEdges.add(here);
		}

		firstTrack.addAll(backEdges);

		//firstTrack.forEach(e -> System.out.println(e.getFromId() + "->" + e.getToId()));

		double length = TourUtils.getPathLength(firstTrack);
		double penalty = TourUtils.tourPenalty(firstTrack, length, false);

		System.out.println(length);
		System.out.println(penalty);
	}

	@Test
	public void TourRoundnessTest() {
		TourGraph g = TourGraph.graphFromCSV(PATH_TO_GRAPH, SRID);
		g.graphToGeojson("minimal.geojson");

		List<TourEdge> firstTrack = new ArrayList<>();
		firstTrack.add(g.getEdge(2160, 1864));
		firstTrack.add(g.getEdge(1864, 1863));
		firstTrack.add(g.getEdge(1863, 1869));
		firstTrack.add(g.getEdge(1869, 2160));

		double length1 = TourUtils.getPathLength(firstTrack);
		double penalty1 = TourUtils.tourPenalty(firstTrack, length1, false);


		List<TourEdge> secondTrack = new ArrayList<>(firstTrack);
		secondTrack.add(0, g.getEdge(2599,2160));
		secondTrack.add(g.getEdge(2160,2599));

		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();

		double length2 = TourUtils.getPathLength(secondTrack);
		double penalty2 = TourUtils.tourPenalty(secondTrack, length2, false);

		System.out.println(penalty1);
		System.out.println(penalty2);

		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();


		List<TourEdge> badBadBadTrack = new ArrayList<>();
		badBadBadTrack.add(g.getEdge(2599, 2160));
		badBadBadTrack.add(g.getEdge(2160, 2599));


		badBadBadTrack.add(g.getEdge(2599, 2160));
		badBadBadTrack.add(g.getEdge(2160, 2599));


		badBadBadTrack.add(g.getEdge(2599, 2160));
		badBadBadTrack.add(g.getEdge(2160, 2599));


		badBadBadTrack.add(g.getEdge(2599, 2160));
		badBadBadTrack.add(g.getEdge(2160, 2599));


		badBadBadTrack.add(g.getEdge(2599, 2160));
		badBadBadTrack.add(g.getEdge(2160, 2599));

		double length3 = TourUtils.getPathLength(badBadBadTrack);
		double penalty3 = TourUtils.tourPenalty(badBadBadTrack, length3, false);

		System.out.println(penalty3);

	}
}