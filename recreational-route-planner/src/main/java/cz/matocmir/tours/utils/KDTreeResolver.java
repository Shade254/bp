package cz.matocmir.tours.utils;

/**
 * Interface for using <code>KDTree</code> with any object.
 */
public interface KDTreeResolver<V> {

	/**
	 * Computed distance between object and given coordinates in meters.
	 */
	public double computeDistance(V v1, double[] coords);

	/**
	 * Returns coordinates for specified object.
	 */
	public double[] getCoordinates(V v1);

}
