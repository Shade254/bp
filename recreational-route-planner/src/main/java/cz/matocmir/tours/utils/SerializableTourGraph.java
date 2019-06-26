package cz.matocmir.tours.utils;

import cz.matocmir.tours.model.TourEdge;
import cz.matocmir.tours.model.TourNode;

import java.io.Serializable;

public class SerializableTourGraph implements Serializable {

	private static final long serialVersionUID = -2478003262483198242L;

	private TourNode[] nodes;
	private TourEdge[] edges;

	private SerializableTourGraph() {
	}

	public SerializableTourGraph(TourNode[] nodes, TourEdge[] edges) {
		super();
		this.nodes = nodes;
		this.edges = edges;
	}


	public TourNode[] getNodes() {
		return nodes;
	}

	public TourEdge[] getEdges() {
		return edges;
	}
}
