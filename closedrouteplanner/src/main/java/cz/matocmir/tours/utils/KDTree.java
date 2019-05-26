package cz.matocmir.tours;

import net.sf.javaml.core.kdtree.KDTree;

import java.util.*;
/**
 * Wrapper class, which allows to use <code>net.sf.javaml.core.kdtree</code> with any object.
 */
public class KDTree2<V> {

	private final net.sf.javaml.core.kdtree.KDTree kdTree;
	private final KDTreeResolver<V> treeResolver;
	private final ObjectOrdering objectOrdering;
	private Map<List<Double>, List<V>> usedCoords;
	private int treeSize;

	/**
	 * Constructs KDTree2.
	 *
	 * @param kdTreeDim    Dimension of the tree
	 * @param treeResolver Implementation of the interface for objects to be stored in the tree, which computes necesarry data
	 * @param elements     Elements to be inserted to KD-tree
	 */
	public KDTree2(int kdTreeDim, KDTreeResolver<V> treeResolver, ObjectOrdering objectOrdering,
			Collection<? extends V> elements) {
		this.kdTree = new net.sf.javaml.core.kdtree.KDTree(kdTreeDim);
		this.treeResolver = treeResolver;
		this.usedCoords = new HashMap<>();
		this.objectOrdering = objectOrdering;
		insertAll(elements);
	}

	/**
	 * Constructs KDTree2.
	 *
	 * @param kdTreeDim    Dimension of the tree
	 * @param treeResolver Implementation of the interface for objects to be stored in the tree, which computes necesarry data
	 */
	public KDTree2(int kdTreeDim, KDTreeResolver<V> treeResolver) {
		this(kdTreeDim, treeResolver, ObjectOrdering.OLD_FIRST, Collections.emptyList());
	}

	public KDTree2(int kdTreeDim, KDTreeResolver<V> treeResolver, Collection<? extends V> elements) {
		this(kdTreeDim, treeResolver, ObjectOrdering.OLD_FIRST, elements);
	}

	public KDTreeResolver<V> getTreeResolver() {
		return treeResolver;
	}

	/**
	 * Returns all objects in the tree that are closer then specified distance from the specified coordinates
	 */
	public ArrayList<V> getNearestNodesCloserThan(double[] coordinates, double desiredDistance) {

		int n = 50;

		ArrayList<V> nNearest = getNNearestNodesWithMaxDistance(coordinates, n, desiredDistance);

		//the function called above performs prunning, thus if n > nNearest.size then the list was prunner based on distance and thus can be returned
		while (n == nNearest.size()) {
			//increasing n while the lsit is prunned based on number
			n = n * 3;
			nNearest = getNNearestNodesWithMaxDistance(coordinates, n, desiredDistance);
		}

		return nNearest;

	}

	/**
	 * Returns maximum N closest objects in the tree from the specified coordinates. Returns only objects closer than specified distance
	 */
	public ArrayList<V> getNNearestNodesWithMaxDistance(double[] coords, int n, double maxDistance) {

		int numOfNeighbors = Math.min(n, innerTreeSize());
		Object[] nearestNodes = kdTree.nearest(coords, numOfNeighbors);

		int distanceUpperBound = nearestNodes.length;

		//prunes those too far away
		while (distanceUpperBound > 0
				&& treeResolver.computeDistance(((List<V>) nearestNodes[distanceUpperBound - 1]).get(0), coords)
				> maxDistance)
			distanceUpperBound--;

		ArrayList<V> prunnedConvertedList = new ArrayList<>();

		for (int i = 0; i < distanceUpperBound; i++) {
			prunnedConvertedList.addAll(((List<V>) nearestNodes[i]));
		}

		if (prunnedConvertedList.size() <= n) {
			return prunnedConvertedList;
		} else {
			return new ArrayList<>(prunnedConvertedList.subList(0, n));
		}
	}

	/**
	 * Returns minimum N objects from the tree, closest from the specified coords.
	 * Nodes closer than <code>distanceToForceMerge</code> are returned, even if there is more than N nodes.
	 * Nodes further than <code>maxDistance</code> are never returned, even if there is less than N nodes.
	 */
	public ArrayList<V> getNNearestNodesWithForcedDistanceToMergeAndMaxDistance(double[] coords, int n,
			double distanceToForceMerge, double maxDistance) {

		if (distanceToForceMerge > maxDistance) {
			throw new IllegalArgumentException(
					"distance to force merge must be smaller or equal to maximalDisance allowed to merge");
		}
		ArrayList<V> nodes = getNearestNodesCloserThan(coords, maxDistance); //maxDistance fulfilled

		//check Nth node
		if (nodes.size() <= n) {
			return nodes;
		} else {
			double distanceToNthNode = treeResolver.computeDistance(nodes.get(n - 1), coords);

			if (distanceToNthNode > distanceToForceMerge) { //all force merged included, return sublist of length N
				return new ArrayList<>(nodes.subList(0, n));
			} else { //not all to force merge included, search for the boundary

				for (int i = n; i < nodes.size(); i++) {
					if (treeResolver.computeDistance(nodes.get(i), coords) > distanceToForceMerge) {
						//return without current
						return new ArrayList<>(nodes.subList(0, i));
					}
				}

				return nodes; //exactly all nodes need to be returned

			}
		}

	}

	/**
	 * Returns nearest N nodes from specified coordinates
	 */
	public ArrayList<V> getNNearestNodes(double[] coords, int n) {
		return getNNearestNodesWithMaxDistance(coords, n, Double.POSITIVE_INFINITY);
	}

	/**
	 * Return neerest node in the tree from specified coordinates
	 */
	public V getNearestNode(double[] coords) {
		return getNNearestNodes(coords, 1).get(0);
	}

	public void insertAll(Collection<? extends V> c) throws IllegalArgumentException {
		c.forEach(this::insert);
	}

	/**
	 * Inserts object to tree.
	 */
	public void insert(V object) throws IllegalArgumentException {

		double[] coords = treeResolver.getCoordinates(object);
		List<Double> coordsList = getCoordsList(coords);

		//get list of nodes with this coordiantes
		List<V> listOfElements = usedCoords.get(coordsList);

		if (listOfElements == null) {
			//this coordinates were not used in the tree yet
			//create new list and push it to the tree and to the map
			listOfElements = new ArrayList<>();
			usedCoords.put(coordsList, listOfElements);
			kdTree.insert(coords, listOfElements);
		}

		//add object to the list
		if (objectOrdering == ObjectOrdering.NEW_FIRST) {
			listOfElements.add(0, object);
		} else {
			listOfElements.add(object);
		}

		treeSize++;
	}

	private List<Double> getCoordsList(double[] coords) {
		List<Double> coordsList = new ArrayList<>();

		for (int i = 0; i < coords.length; i++) {
			coordsList.add(coords[i]);
		}
		return coordsList;
	}

	/**
	 * Deletes object from tree.
	 */
	public void delete(V object) {

		double[] coords = treeResolver.getCoordinates(object);
		List<Double> coordsList = getCoordsList(coords);

		//get list of nodes with this coordiantes
		List<V> listOfElements = usedCoords.get(coordsList);

		if (listOfElements == null) {
			//do nothing, object not in the tree
		} else {
			if (listOfElements.remove(object)) {
				treeSize--;

				//if the element was the only in the list, then remove it
				if (listOfElements.isEmpty()) {
					usedCoords.remove(coordsList);
					kdTree.delete(coords);
				}
			}
		}
	}

	/**
	 * Deletes all objects with given coordinates from the tree.
	 */
	public void delete(double[] coords) {

		List<Double> coordsList = getCoordsList(coords);

		//get list of nodes with this coordiantes
		List<V> listOfElements = usedCoords.get(coordsList);

		if (listOfElements == null) {
			//do nothing, no objects with this coordinates in the tree
		} else {
			//some objects present
			//remove from map and tree and decrease size
			usedCoords.remove(coordsList);
			kdTree.delete(coords);
			treeSize -= listOfElements.size();
		}
	}

	/**
	 * Returns num of elements in the tree.
	 */
	public int size() {
		return treeSize;
	}

	private int innerTreeSize() {
		return usedCoords.size();
	}

	public enum ObjectOrdering {
		OLD_FIRST, NEW_FIRST;
	}
}
