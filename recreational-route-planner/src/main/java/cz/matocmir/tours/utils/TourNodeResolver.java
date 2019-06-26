package cz.matocmir.tours.utils;

import cz.matocmir.tours.model.TourNode;

public class TourNodeResolver<V extends TourNode> implements KDTreeResolver<V> {
	@Override
	public double computeDistance(V v1, double[] coords) {
		return TourUtils.computeEuclideanDistance(v1.getLatProjected(), v1.getLonProjected(), coords[1], coords[0]);
	}

	@Override
	public double[] getCoordinates(V v1) {
		return new double[] { v1.getLonProjected(), v1.getLatProjected() };
	}
}
