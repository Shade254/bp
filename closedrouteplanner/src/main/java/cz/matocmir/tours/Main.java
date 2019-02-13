package cz.matocmir.tours;

import java.io.*;

public class Main {

	public static void main(String[] args) {
		final String graphPath = "./src/main/resources/prague.csv";
		final int startingNode = 3;
		System.out.println("Loading graph from file: " + graphPath);
		TourGraph loadedGraph = loadGraph(graphPath);
		System.out.println("Graph loaded ... dumping");
		loadedGraph.dumpToGeoJson("test.geojson");
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
				return new TourEdge(from, to, Integer.parseInt(pom[7]));
			}).forEach(g::addEdge);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return g;
	}
}
