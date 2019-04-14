package cz.matocmir.tours.model;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.umotional.basestructures.GraphStructure;
import jersey.repackaged.com.google.common.collect.HashBasedTable;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TourGraph implements GraphStructure<TourNode, TourEdge>, Serializable {
	private static final Logger log = Logger.getLogger(TourGraph.class);
	private HashMap<Integer, TourNode> nodes = new HashMap<>();
	private HashMap<EdgeId, TourEdge> edges = new HashMap<>();
	private HashMap<Integer, List<TourEdge>> outgoingEdges = new HashMap<>();
	private HashMap<Integer, List<TourEdge>> incomingEdges = new HashMap<>();

	public TourGraph() {
	}

	public void addEdge(TourEdge edge) {
		int from = edge.getFromId(), to = edge.getToId();

		if (!this.containsNode(edge.getFrom())) {
			this.addNode(edge.getFrom());
		}

		if (!this.containsNode(edge.getTo())) {
			this.addNode(edge.getTo());
		}

		edges.put(new EdgeId(from, to), edge);

		outgoingEdges.get(from).add(edge);
		incomingEdges.get(to).add(edge);
	}

	public void addNode(TourNode node) {
		if (!this.containsNode(node.getId())) {
			nodes.put(node.getId(), node);
			outgoingEdges.putIfAbsent(node.getId(), new ArrayList<>());
			incomingEdges.putIfAbsent(node.getId(), new ArrayList<>());
		}
	}

	public void addNode(int id, double lat, double lon) {
		if (!this.containsNode(id)) {
			nodes.put(id, new TourNode(lat, lon, id));
			outgoingEdges.putIfAbsent(id, new ArrayList<>());
			incomingEdges.putIfAbsent(id, new ArrayList<>());
		}
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

	private static class EdgeId {
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

	public void graphToGeojson(String jsonFile) {
		try (PrintStream c = new PrintStream(jsonFile)) {

			c.print("{\n" + "  \"type\": \"FeatureCollection\",\n" + "  \"features\": [");
			StringBuilder sb = new StringBuilder();

			for (TourNode node : nodes.values()) {
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

	public static TourGraph graphFromCSV(String graphPath) {
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
				TourNode from = new TourNode(Double.parseDouble(pom[3]), Double.parseDouble(pom[4]),
						Integer.parseInt(pom[0]));
				TourNode to = new TourNode(Double.parseDouble(pom[5]), Double.parseDouble(pom[6]),
						Integer.parseInt(pom[1]));
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

	public boolean graphToKryo(String outputFile) {
		Kryo kryo = new Kryo();
		kryo.register(TourGraph.class);
		kryo.register(HashBasedTable.class, new JavaSerializer());

		try {
			Output output = new Output(new FileOutputStream(outputFile));
			kryo.writeObject(output, this);
			output.close();

			return true;
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		}

		return true;
	}

	public static TourGraph graphFromKryo(String inputFile) {
		Kryo kryo = new Kryo();
		kryo.register(TourGraph.class);
		kryo.register(HashBasedTable.class, new JavaSerializer());

		Input in = null;
		try {
			in = new Input(new FileInputStream(inputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		System.out.println("Starting to read");
		TourGraph serializableTnsGraph = kryo.readObject(in, TourGraph.class);
		in.close();
		System.out.println("Done");

		return serializableTnsGraph;
	}
}
