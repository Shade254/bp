package cz.matocmir.tours.model;

import com.umotional.basestructures.GraphStructure;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TourGraph implements GraphStructure<TourNode, TourEdge> {
	private HashMap<Integer, TourNode> nodes = new HashMap<>();
	private HashMap<EdgeId, TourEdge> edges = new HashMap<>();
	private HashMap<Integer, List<TourEdge>> outgoingEdges = new HashMap<>();
	private HashMap<Integer, List<TourEdge>> incomingEdges = new HashMap<>();



	public void addEdge(TourEdge edge){
		if(this.containsEdge(edge.getFromId(), edge.getToId())){
			return;
		}

		if(!this.containsNode(edge.getFromId())){
			this.addNode(edge.getFrom());
		}

		if(!this.containsNode(edge.getToId())){
			this.addNode(edge.getTo());
		}

		EdgeId eId = new EdgeId(edge.getFromId(), edge.getToId());
		edges.put(eId, edge);

		outgoingEdges.putIfAbsent(edge.getFromId(), new ArrayList<>());
		incomingEdges.putIfAbsent(edge.getToId(), new ArrayList<>());

		outgoingEdges.get(edge.getFromId()).add(edge);
		incomingEdges.get(edge.getToId()).add(edge);
	}

	public void addNode(TourNode node){
		if(!this.containsNode(node.getId())){
			nodes.put(node.getId(), node);
		}
	}

	public void addNode(int id, double lat, double lon){
		if(!this.containsNode(id)){
			nodes.put(id, new TourNode(lat, lon, id));
		}
	}

	@Override
	public boolean containsNode(TourNode tourNode) {
		return containsNode(tourNode.getId());
	}

	@Override
	public boolean containsNode(int i) {
		return (nodes.get(i)!=null);
	}

	@Override
	public TourNode getNode(int i) {
		return nodes.get(i);
	}

	@Override
	public boolean containsEdge(TourEdge tourEdge) {
		return containsEdge(tourEdge.getFromId(), tourEdge.getToId());
	}

	@Override
	public boolean containsEdge(int i, int i1) {
		return (edges.get(new EdgeId(i, i1))!=null);
	}

	@Override
	public TourEdge getEdge(int i, int i1) {
		return edges.get(new EdgeId(i, i1));
	}

	@Override
	public List<TourEdge> getInEdges(TourNode tourNode) {
		return getInEdges(tourNode.getId());
	}

	@Override
	public List<TourEdge> getInEdges(int i) {
		return incomingEdges.get(i);
	}

	@Override
	public List<TourEdge> getOutEdges(TourNode tourNode) {
		return getOutEdges(tourNode.getId());
	}

	@Override
	public List<TourEdge> getOutEdges(int i) {
		return outgoingEdges.get(i) == null ? new ArrayList<>() : outgoingEdges.get(i);
	}

	@Override
	public Collection<TourNode> getAllNodes() {
		return nodes.values();
	}

	@Override
	public Collection<TourEdge> getAllEdges() {
		return edges.values();
	}

	private class EdgeId {
		final int fromNodeId;
		final int toNodeId;

		public EdgeId(int fromNodeId, int toNodeId) {
			super();
			this.fromNodeId = fromNodeId;
			this.toNodeId = toNodeId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (fromNodeId ^ (fromNodeId >>> 32));
			result = prime * result + (toNodeId ^ (toNodeId >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			TourGraph.EdgeId other = (TourGraph.EdgeId) obj;
			if (fromNodeId != other.fromNodeId)
				return false;
			return toNodeId == other.toNodeId;
		}

		@Override
		public String toString() {
			return String.format("%d-->%d", this.fromNodeId, this.toNodeId);
		}
	}

	public void dumpToGeoJson(String jsonFile){
		try(PrintStream c = new PrintStream(jsonFile)) {

			c.print("{\n" + "  \"type\": \"FeatureCollection\",\n" + "  \"features\": [");
			StringBuilder sb = new StringBuilder();

			for (TourNode node : nodes.values()) {
				sb.append(String.format("{\n" + "      \"type\": \"Feature\",\n" + "      \"geometry\": {\n" + "        \"type\": \"Point\",\n" + "        \"coordinates\": [%.5f, %.5f]\n" + "      },\n"
								+ "      \"properties\": {\n" + "        \"name\": \"%s\"\n" + "      }\n" + "    },",
						node.getLongitude(), node.getLatitude(), node.getId() + ""));
			}

			for(TourEdge edge : edges.values()){
				sb.append("{\n" + "      \"type\": \"Feature\",\n" + "      \"geometry\": {\n"
						+ "        \"type\": \"LineString\",\n" + "        \"coordinates\": [");
				sb.append(String.format("[%.5f, %.5f],", edge.getFrom().getLongitude(), edge.getFrom().getLatitude()));
				sb.append(String.format("[%.5f, %.5f]]", edge.getTo().getLongitude(), edge.getTo().getLatitude()));
				sb.append("},\n");
				sb.append(String.format("\"properties\": {\n" + "        \"name\": \"%s\"\n" + "      }\n" + "    },", edge.getCost()));
			}

			c.print(sb.substring(0, sb.length() - 1));
			c.print("]\n" + "}");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
