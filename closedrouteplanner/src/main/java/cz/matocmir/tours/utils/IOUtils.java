package cz.matocmir.tours.utils;

import cz.matocmir.tours.model.TourEdge;
import cz.matocmir.tours.model.TourGraph;
import cz.matocmir.tours.model.TourNode;
import cz.matocmir.tours.model.TreeNode;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class IOUtils {

	public static TourGraph loadGraph(String graphPath) {
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

	public static void visualizeNodes(List<TourNode> nodes, String jsonFile){
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

	public static void visualizeEdges(List<TourEdge> edges, String jsonFile){
		try(PrintStream c = new PrintStream(jsonFile)) {

			c.print("{\n" + "  \"type\": \"FeatureCollection\",\n" + "  \"features\": [");
			StringBuilder sb = new StringBuilder();

			for(TourEdge edge : edges){
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

	public static void visualizePath(List<TreeNode> treeNodes, String jsonFile){
		List<TourEdge> edges = treeNodes.stream().map(TreeNode::getEdgeFromParent).filter(Objects::nonNull).collect(
				Collectors.toList());
		List<TourNode> nodes = treeNodes.stream().map(TreeNode::getNode).filter(Objects::nonNull).collect(Collectors.toList());

		try(PrintStream c = new PrintStream(jsonFile)) {

			c.print("{\n" + "  \"type\": \"FeatureCollection\",\n" + "  \"features\": [");
			StringBuilder sb = new StringBuilder();

			for (TourNode node : nodes) {
				sb.append(String.format("{\n" + "      \"type\": \"Feature\",\n" + "      \"geometry\": {\n" + "        \"type\": \"Point\",\n" + "        \"coordinates\": [%.5f, %.5f]\n" + "      },\n"
								+ "      \"properties\": {\n" + "        \"name\": \"%s\"\n" + "      }\n" + "    },",
						node.getLongitude(), node.getLatitude(), node.getId() + ""));
			}

			for(TourEdge edge : edges){
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
