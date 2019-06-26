package cz.matocmir.tours.utils;


import cz.matocmir.tours.model.*;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

public class IOUtils {
	private static final Logger log = Logger.getLogger(IOUtils.class);

	public static void visualizeNodes(List<TourNode> nodes, String jsonFile) {
		try (PrintStream c = new PrintStream(jsonFile)) {

			c.print("{\n" + "  \"type\": \"FeatureCollection\",\n" + "  \"features\": [");
			StringBuilder sb = new StringBuilder();

			for (TourNode node : nodes) {
				sb.append(String.format("{\n" + "      \"type\": \"Feature\",\n" + "      \"geometry\": {\n"
								+ "        \"type\": \"Point\",\n" + "        \"coordinates\": [%.5f, %.5f]\n" + "      },\n"
								+ "      \"properties\": {\n" + "        \"name\": \"%s\"\n" + "      }\n" + "    },",
						node.getLongitude(), node.getLatitude(), node.getId() + ""));
			}

			c.print(sb.substring(0, sb.length() - 1));
			c.print("]\n" + "}");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static String visualizeEdges(List<TourEdge> edges) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n" + "  \"type\": \"FeatureCollection\",\n" + "  \"features\": [");

		for (TourEdge edge : edges) {
			sb.append("{\n" + "      \"type\": \"Feature\",\n" + "      \"geometry\": {\n"
					+ "        \"type\": \"LineString\",\n" + "        \"coordinates\": [");
			sb.append(String.format("[%.5f, %.5f],", edge.getFrom().getLongitude(), edge.getFrom().getLatitude()));
			sb.append(String.format("[%.5f, %.5f]]", edge.getTo().getLongitude(), edge.getTo().getLatitude()));
			sb.append("},\n");
			sb.append(String.format("\"properties\": {\n" + "        \"name\": \"%s\"\n" + "      }\n" + "    },",
					edge.getCost()));
		}

		String finalString = sb.substring(0, sb.length() - 1);
		finalString += ("]\n" + "}");
		return finalString;
	}


	public static String visualizePath(List<TourEdge> edges, TourNode start, TourNode end, TourNode turn) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n" + "  \"type\": \"FeatureCollection\",\n" + "  \"features\": [");

		for (TourEdge edge : edges) {
			sb.append("{\n" + "      \"type\": \"Feature\",\n" + "      \"geometry\": {\n"
					+ "        \"type\": \"LineString\",\n" + "        \"coordinates\": [");
			sb.append(String.format("[%.5f, %.5f],", edge.getFrom().getLongitude(), edge.getFrom().getLatitude()));
			sb.append(String.format("[%.5f, %.5f]]", edge.getTo().getLongitude(), edge.getTo().getLatitude()));
			sb.append("},\n");
			sb.append(String.format("\"properties\": {\n" + "        \"name\": \"%s\"\n" + "      }\n" + "    },",
					edge.getCost()));
		}

		sb.append("{\n" + "      \"type\": \"Feature\",\n" + "      \"geometry\": {\n"
				+ "        \"type\": \"Point\",\n" + "        \"coordinates\": [");
		sb.append(String.format("[%.5f, %.5f],", start.getLongitude(), start.getLatitude()));
		sb.append("},\n");
		sb.append(String.format("\"properties\": {\n" + "        \"name\": \"%s\"\n" + "      }\n" + "    },",
				start.id));

		sb.append("{\n" + "      \"type\": \"Feature\",\n" + "      \"geometry\": {\n"
				+ "        \"type\": \"Point\",\n" + "        \"coordinates\": [");
		sb.append(String.format("[%.5f, %.5f],", end.getLongitude(), end.getLatitude()));
		sb.append("},\n");
		sb.append(String.format("\"properties\": {\n" + "        \"name\": \"%s\"\n" + "      }\n" + "    },",
				end.id));

		sb.append("{\n" + "      \"type\": \"Feature\",\n" + "      \"geometry\": {\n"
				+ "        \"type\": \"Point\",\n" + "        \"coordinates\": [");
		sb.append(String.format("[%.5f, %.5f],", turn.getLongitude(), turn.getLatitude()));
		sb.append("},\n");
		sb.append(String.format("\"properties\": {\n" + "        \"name\": \"%s\"\n" + "      }\n" + "    },",
				turn.id));


		String finalString = sb.substring(0, sb.length() - 1);
		finalString += ("]\n" + "}");
		return finalString;
	}

	public static String nodeToFeatureString(TourNode node) {
		String template = "{\n" + "  \"type\": \"Feature\",\n" + "  \"geometry\": {\n" + "    \"type\": \"Point\",\n"
				+ "    \"coordinates\": [%.5f, %.5f]\n" + "  },\n" + "  \"properties\": {\n" + "    \"name\": \"%s\"\n"
				+ "  }\n" + "}";
		return String.format(template, node.getLongitude(), node.getLatitude(), node.getId());
	}

	public static Feature nodeToFeature(TourNode node){
		return new Feature(Type.Feature, new Geometry(Type.Point, new Double[]{node.getLongitude(), node.getLatitude()}), null);
	}

	public static Feature edgeToFeature(TourEdge e){
		return new Feature(Type.Feature, new Geometry(Type.LineString, new Object[]{new Double[]{e.getFrom().getLongitude(), e.getFrom().getLatitude()}, new Double[]{e.getTo().getLongitude(), e.getTo().getLatitude()}}), null);
	}
}
