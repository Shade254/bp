package cz.matocmir.tours;

import cz.matocmir.tours.backpath.BackPathFinder;
import cz.matocmir.tours.forwardpath.CandidateFinder;
import cz.matocmir.tours.forwardpath.CandidatesPicker;
import cz.matocmir.tours.model.*;
import cz.matocmir.tours.utils.IOUtils;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

	public static void main(String[] args) {
		final String graphPath = "./src/main/resources/prague.csv";
		System.out.println("Loading graph from file: " + graphPath);
		TourGraph loadedGraph = IOUtils.loadGraph(graphPath);

		//System.out.println("Graph loaded ... dumping");
		//loadedGraph.dumpToGeoJson("test.geojson");

		while(true){
			solve(loadedGraph);
		}

	}

	private static void solve(TourGraph graph){
		Scanner scan = new Scanner(System.in);
		CandidateFinder canFinder = new CandidateFinder(graph);

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
		TourNode startingNode = graph.getNode(startingId);

		System.out.println("Running forward search in interval " + minLength + " - " + maxLength + " m");
		List<Candidate> cands = canFinder.forwardSearch(startingNode, minLength, maxLength);
		System.out.println("Found: " + cands.size() + " candidates");
		IOUtils.visualizeNodes(cands.stream().map(c -> c.correspNode.getNode()).collect(Collectors.toList()),
				"found.geojson");

		int numOfCycles;
		System.out.print("Enter num of cycles: \n>");
		numOfCycles = scan.nextInt();

		BackPathFinder backPathFinder = new BackPathFinder(graph);
		CandidatesPicker candidatesPicker = new CandidatesPicker(cands, startingNode, minLength);

		int foundCycles = 0;
		List<TourEdge> foundPath;
		for(int i = 0;i<cands.size();i++) {
			Candidate randomCand = candidatesPicker.selectCandidate();
			IOUtils.visualizePath(randomCand.correspNode.pathFromRoot(), "forw_path_" + i + ".geojson");

			System.out.println("Finding back path from node " + randomCand.correspNode.getNode().getId());
			foundPath = backPathFinder.getPathBack(randomCand, startingNode, maxLength, minLength);

			if (foundPath != null) {
				IOUtils.visualizeEdges(foundPath, "bestWalk_" + i + ".geojson");
				foundCycles++;
			} else {
				System.out.println("Walk not found");
			}

			if(foundCycles >= numOfCycles){
				System.out.println("All cycles found");
				break;
			}
		}

		if(foundCycles < numOfCycles){
			System.out.println("Not enough cycles found");
		}

		System.out.println("=================================");
	}
}
