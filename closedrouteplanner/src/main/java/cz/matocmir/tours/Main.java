package cz.matocmir.tours;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

	public static void main(String[] args) {
		final String graphPath = "./src/main/resources/prague.csv";
		System.out.println("Loading graph from file: " + graphPath);
		TourGraph loadedGraph = loadGraph(graphPath);

		//System.out.println("Graph loaded ... dumping");
		//loadedGraph.dumpToGeoJson("test.geojson");

		TourNode startingNode = loadedGraph.getNode(17500);
		final int minLength = 1500;
		final int maxLength = 5000;

		List<Candidate> cands = forwardSearch(loadedGraph, startingNode, minLength, maxLength);
		System.out.println("Found: " + cands.size());
		visualizeNodes(cands.stream().map(c -> c.correspNode.getNode()).collect(Collectors.toList()), "found.geojson");

		//TODO - random candidate, extract path, visualize it
		//TODO - add roundness criterium
	}

	private static void visualizeNodes(List<TourNode> nodes, String jsonFile){
		try(PrintStream c = new PrintStream(jsonFile)) {

			c.print("{\n" + "  \"type\": \"FeatureCollection\",\n" + "  \"features\": [");
			StringBuilder sb = new StringBuilder();

			for (TourNode node : nodes) {
				sb.append(String.format("{\n" + "      \"type\": \"Feature\",\n" + "      \"geometry\": {\n" + "        \"type\": \"Point\",\n" + "        \"coordinates\": [%.5f, %.5f]\n" + "      },\n"
								+ "      \"properties\": {\n" + "        \"name\": \"%s\"\n" + "      }\n" + "    },",
						node.getLongitude(), node.getLatitude(), node.getId() + ""));
			}

			c.print(sb.substring(0, sb.length() - 1));
			c.print("]\n" + "}");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void visualizePath(List<TreeNode> nodes){

	}

	private static TourGraph loadGraph(String graphPath) {
		TourGraph g = new TourGraph();

		try (BufferedReader reader = new BufferedReader(new FileReader(graphPath))) {
			//column titles
			reader.readLine();

			reader.lines()/*.limit(20000)*/.map(line -> {
				String[] pom = line.split(";");
				TourNode from = new TourNode(Double.parseDouble(pom[3]), Double.parseDouble(pom[4]),
						Integer.parseInt(pom[0]));
				TourNode to = new TourNode(Double.parseDouble(pom[5]), Double.parseDouble(pom[6]),
						Integer.parseInt(pom[1]));
				return new TourEdge(from, to, Integer.parseInt(pom[7]), (int)Double.parseDouble(pom[2]));
			}).forEach(g::addEdge);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return g;
	}

	private static ArrayList<Candidate> forwardSearch(TourGraph graph, TourNode startNode, int minLength,
			int maxLength) {
		final double epsilon = 0.001;
		ArrayList<Candidate> candidates = new ArrayList<>();

		Queue<Candidate> q = new PriorityQueue<>(Comparator.comparingInt(o -> o.weight));
		q.add(new Candidate(new TreeNode(null, null, startNode), 0, 0));
		HashSet<Integer> addedIds = new HashSet<>();

		while (!q.isEmpty()) {
			Candidate cur = q.poll();
			TreeNode curN = cur.correspNode;
			if (addedIds.add(curN.getNode().getId())) {
				System.out.println("Candidate " + cur.toString());
				boolean boundaryNode = false;

				List<TourEdge> outEdges = graph.getOutEdges(curN.getNode().getId());
				for (TourEdge e : outEdges) {
					System.out.println("Edge " + e.toString());
					Candidate cn = new Candidate(new TreeNode(e, curN, e.getTo()), cur.weight + e.getCost(),
							cur.length + (int) e.getLengthInMeters());
					List<TourEdge> cnOutEdges = graph.getOutEdges(cn.correspNode.getNode().getId());
					HashSet<Integer> encounteredIds = new HashSet<>(); // Cycle detection


					while (cnOutEdges.size() == 1 && cn.length <= maxLength + epsilon) {
						e = cnOutEdges.get(0);
						cn = new Candidate(new TreeNode(e, cn.correspNode, e.getTo()), cn.weight + e.getCost(),
								cn.length + (int) e.getLengthInMeters());
						cnOutEdges = graph.getOutEdges(cn.correspNode.getNode().getId());

						if (!encounteredIds.add(cn.correspNode.getNode().getId())) {
							cn.length = Integer.MAX_VALUE;
							break;
						}
					}

					double tourL = cn.length + TourUtils
							.computeGreatCircleDistance(cn.correspNode.getNode().getLatitude(), cn.correspNode.getNode().getLongitude(),
									startNode.getLatitude(), startNode.getLongitude());
					if (tourL <= maxLength + epsilon) {
						System.out.println("Adding " + cn.toString() + " " + TourUtils
								.computeGreatCircleDistance(e.getTo().getLatitude(), e.getTo().getLongitude(),
										startNode.getLatitude(), startNode.getLongitude()));
						if (!addedIds.contains(e.getToId())) {
							q.add(cn);
						}
					} else if (cur.length > 0) {
						boundaryNode = true;
					}

				}

				double minlenMinCurlen = minLength - cur.length;
				double distance = TourUtils.computeGreatCircleDistance(curN.getNode().getLatitude(), curN.getNode().getLongitude(),
						startNode.getLatitude(), startNode.getLongitude());
				if (boundaryNode || minlenMinCurlen < 0 || distance + epsilon >= minlenMinCurlen) {
					candidates.add(cur);
				}
			}
		}
		return candidates;
	}
}
