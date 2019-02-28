package cz.matocmir.tours;

import cz.matocmir.tours.model.TourEdge;
import cz.matocmir.tours.model.TourGraph;
import cz.matocmir.tours.point2point.PointToPointFinder;
import cz.matocmir.tours.utils.IOUtils;

import java.util.List;
import java.util.Scanner;

public class PointToPointMain {
	public static void main(String[] args) {
		final String graphPath = "./src/main/resources/prague.csv";
		System.out.println("Loading graph from file: " + graphPath);
		TourGraph loadedGraph = IOUtils.loadGraph(graphPath);

		while(true){
			solve(loadedGraph);
		}
	}

	private static void solve(TourGraph graph){
		Scanner scan = new Scanner(System.in);

		int desLength;
		int percentTolerance;
		System.out.print("Enter desired length (in  m): \n>");
		desLength = scan.nextInt();
		System.out.print("Enter maximum deviation (in  %): \n>");
		percentTolerance = scan.nextInt();

		int minLength = desLength - (desLength * percentTolerance / 100);
		int maxLength = desLength + (desLength * percentTolerance / 100);

		int startingId;
		System.out.print("Enter starting node id: \n>");
		startingId = scan.nextInt();

		int goalId;
		System.out.print("Enter end node node id: \n>");
		goalId = scan.nextInt();

		PointToPointFinder finder = new PointToPointFinder(graph, minLength, maxLength, startingId, goalId);
		List<List<TourEdge>> result = finder.searchForPaths(5);
		int counter = 0;
		for(List<TourEdge> path : result){
			IOUtils.visualizeEdges(path, counter++ + "_path.geojson");
		}
	}
}
