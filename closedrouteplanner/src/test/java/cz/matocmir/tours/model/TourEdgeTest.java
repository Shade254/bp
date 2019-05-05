package cz.matocmir.tours.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class TourEdgeTest {

	@Test
	public void roundnessPenalty() {
		// nodes in shape of a rectangle
		TourNode n1 = new TourNode(50.082390, 14.504781, 1);
		TourNode n2 = new TourNode(50.081282, 14.505167, 2);
		TourNode n3 = new TourNode(50.081013, 14.503171, 3);
		TourNode n4 = new TourNode(50.082087, 14.502763, 4);

		//nodes further away but parallel with first edge
		TourNode s1 = new TourNode(50.081970, 14.501690, 5);
		TourNode s2 = new TourNode(50.080930, 14.502087, 6);


		TourEdge e1 = new TourEdge(n1, n2, 1.1);
		TourEdge e2 = new TourEdge(n2, n3, 1.1);
		TourEdge e3 = new TourEdge(n2, n1, 1.1);
		TourEdge e4 = new TourEdge(n3, n4, 1.1);
		TourEdge e5 = new TourEdge(s2, s1, 1.1);

		// penalty of right angle, should be close to 0
		double right = e1.roundnessPenalty(e2, e1.getLengthInMeters()/2 + e2.getLengthInMeters()/2,1);

		// further edge should have greater penalty
		double near = e1.roundnessPenalty(e4, e1.getLengthInMeters()/2 + e4.getLengthInMeters()/2 + 100,1);
		double far = e1.roundnessPenalty(e5, e1.getLengthInMeters()/2 + e5.getLengthInMeters()/2 + 100,1);

		// max penalty
		double parallel = e1.roundnessPenalty(e3, e1.getLengthInMeters()/2 + e3.getLengthInMeters()/2, 1);

		assertTrue(right <= near);
		assertTrue(right <= far);

		assertTrue(far < near);

		assertTrue(near < parallel);
	}
}