package cz.matocmir.tours.model;

import com.umotional.basestructures.GraphStructure;
import com.umotional.geotools.Transformer;
import com.vividsolutions.jts.geom.Coordinate;
import cz.matocmir.tours.utils.SerializableTourGraph;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TourGraph implements GraphStructure<TourNode, TourEdge>, Serializable {
	private static final Logger log = Logger.getLogger(TourGraph.class);
	private TIntObjectMap<TourNode> nodes;
	private Map<EdgeId, TourEdge> edges;
	private TIntObjectMap<Collection<TourEdge>> outgoingEdges;
	private TIntObjectMap<Collection<TourEdge>> incomingEdges;


	public TourGraph(){
		init();
	}

	public TourGraph(SerializableTourGraph serializableTourGraph) {
		init();
		System.out.println("Kryo loaded ...");

		for(TourNode m : serializableTourGraph.getNodes()){
			addNode(m);
		}

		for(TourEdge f : serializableTourGraph.getEdges()) {
			addEdge(f);
		}

		System.out.println("Edges added");
	}
	private void init(){
		nodes = new TIntObjectHashMap<>();
		edges = new HashMap<>();
		outgoingEdges = new TIntObjectHashMap<>();
		incomingEdges = new TIntObjectHashMap<>();
	}

	public void addEdge(TourEdge edge) {
		int from = edge.getFromId(), to = edge.getToId();

		assert nodes.get(from) != null
				&& nodes.get(to) != null : "Node has to be in graph builder before inserting edge";

		edges.put(new EdgeId(from, to), edge);

		outgoingEdges.get(from).add(edge);
		incomingEdges.get(to).add(edge);
	}

	public void addNode(TourNode node) {
		assert !this.containsNode(node.getId()) : "Graph already contains node with same ID(" + node.getId() + ")" ;

		nodes.put(node.getId(), node);
		outgoingEdges.put(node.getId(), new ArrayList<>(4));
		incomingEdges.put(node.getId(), new ArrayList<>(4));

	}

	@Override
	public boolean containsNode(TourNode tourNode) {
		return containsNode(tourNode.getId());
	}

	@Override
	public boolean containsNode(int i) {
		return (nodes.get(i) != null);
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
		return (edges.get(new EdgeId(i, i1)) != null);
	}

	@Override
	public TourEdge getEdge(int i, int i1) {
		return edges.get(new EdgeId(i, i1));
	}

	@Override
	public List<TourEdge> getInEdges(TourNode tourNode) {
		return new ArrayList<>(getInEdges(tourNode.getId()));
	}

	@Override
	public List<TourEdge> getInEdges(int i) {
		return incomingEdges.get(i) == null ? new ArrayList<>() : new ArrayList<>(incomingEdges.get(i));
	}

	@Override
	public List<TourEdge> getOutEdges(TourNode tourNode) {
		return getOutEdges(tourNode.getId());
	}

	@Override
	public List<TourEdge> getOutEdges(int i) {
		return outgoingEdges.get(i) == null ? new ArrayList<>() : new ArrayList<>(outgoingEdges.get(i));
	}

	@Override
	public Collection<TourNode> getAllNodes() {
		return nodes.valueCollection();
	}

	@Override
	public Collection<TourEdge> getAllEdges() {
		return edges.values();
	}

	public SerializableTourGraph getSerializableTnsGraph() {
		return new SerializableTourGraph(nodes.valueCollection().toArray(new TourNode[nodes.size()]), edges.values().toArray(
				new TourEdge[0]));
	}

	private static class EdgeId implements Serializable{
		int fromNodeId;
		int toNodeId;

		public EdgeId() {
		}

		public EdgeId(int fromNodeId, int toNodeId) {
			this.fromNodeId = fromNodeId;
			this.toNodeId = toNodeId;
		}

		@Override
		public int hashCode() {
			int result = fromNodeId;
			result = 31 * result + toNodeId;
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

	public void graphToGeojson(String jsonFile) {
		try (PrintStream c = new PrintStream(jsonFile)) {

			c.print("{\n" + "  \"type\": \"FeatureCollection\",\n" + "  \"features\": [");
			StringBuilder sb = new StringBuilder();

			for (TourNode node : nodes.valueCollection()) {
				sb.append(String.format("{\n" + "      \"type\": \"Feature\",\n" + "      \"geometry\": {\n"
								+ "        \"type\": \"Point\",\n" + "        \"coordinates\": [%.5f, %.5f]\n" + "      },\n"
								+ "      \"properties\": {\n" + "        \"name\": \"%s\"\n" + "      }\n" + "    },",
						node.getLongitude(), node.getLatitude(), node.getId() + ""));
			}

			for (TourEdge edge : edges.values()) {
				sb.append("{\n" + "      \"type\": \"Feature\",\n" + "      \"geometry\": {\n"
						+ "        \"type\": \"LineString\",\n" + "        \"coordinates\": [");
				sb.append(String.format("[%.5f, %.5f],", edge.getFrom().getLongitude(), edge.getFrom().getLatitude()));
				sb.append(String.format("[%.5f, %.5f]]", edge.getTo().getLongitude(), edge.getTo().getLatitude()));
				sb.append("},\n");
				sb.append(String.format("\"properties\": {\n" + "        \"name\": \"%s\"\n" + "      }\n" + "    },",
						edge.getCost()));
			}

			c.print(sb.substring(0, sb.length() - 1));
			c.print("]\n" + "}");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static TourGraph graphFromCSV(String graphPath, int projection) {
		Transformer transformer = new Transformer(projection);
		log.info("Loading graph from " + (new File(graphPath).getAbsolutePath()));
		TourGraph g = new TourGraph();
		AtomicInteger counter = new AtomicInteger();

		try (BufferedReader reader = new BufferedReader(new FileReader(graphPath))) {
			long total = Files.lines((new File(graphPath)).toPath(), Charset.defaultCharset()).count();
			log.info("File has " + total + " lines");
			//column titles
			reader.readLine();
			final int[] percent = { 10 };
			reader.lines().map(line -> {
				String[] pom = line.split(";");
				Coordinate projectedFrom = transformer
						.toProjected(new Coordinate(Double.parseDouble(pom[4]), Double.parseDouble(pom[3])));

				TourNode from = new TourNode(Double.parseDouble(pom[3]), Double.parseDouble(pom[4]),
						Integer.parseInt(pom[0]), projectedFrom.y, projectedFrom.x);

				Coordinate projectedTo = transformer
						.toProjected(new Coordinate(Double.parseDouble(pom[6]), Double.parseDouble(pom[5])));
				TourNode to = new TourNode(Double.parseDouble(pom[5]), Double.parseDouble(pom[6]),
						Integer.parseInt(pom[1]), projectedTo.y, projectedTo.x);

				if(!g.containsNode(from)) g.addNode(from);
				if(!g.containsNode(to)) g.addNode(to);

				return new TourEdge(from, to, Double.parseDouble(pom[7]), Double.parseDouble(pom[2]));
			}).forEach(e -> {
				g.addEdge(e);

				counter.getAndIncrement();
				if (total / 100 * percent[0] < counter.get()) {
					log.info(percent[0] + "% done");
					percent[0] += 10;
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
		return g;
	}
}
