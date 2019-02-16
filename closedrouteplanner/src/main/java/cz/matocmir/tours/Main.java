package cz.matocmir.tours;

import cz.matocmir.tours.backpath.BackPathFinder;
import cz.matocmir.tours.forwardpath.CandidateFinder;
import cz.matocmir.tours.forwardpath.CandidatesPicker;
import cz.matocmir.tours.model.*;
import cz.matocmir.tours.utils.IOUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class Main {
	private static final Logger log = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		final String graphPath = "./src/main/resources/prague.csv";
		log.info("Loading graph from file: " + graphPath);
		TourGraph loadedGraph = IOUtils.loadGraph(graphPath);

		//System.out.println("Graph loaded ... dumping");
		//loadedGraph.dumpToGeoJson("test.geojson");

		CandidateFinder canFinder = new CandidateFinder(loadedGraph);

		TourNode startingNode = loadedGraph.getNode(17500);
		final int minLength = 8500;
		final int maxLength = 10000;

		log.info("Running forward search in interval " + minLength + " - " + maxLength + " m");
		List<Candidate> cands = canFinder.forwardSearch(startingNode, minLength, maxLength);
		System.out.println("Found: " + cands.size() + " candidates");
		IOUtils.visualizeNodes(cands.stream().map(c -> c.correspNode.getNode()).collect(Collectors.toList()),
				"found.geojson");

		int numOfTries = 4;
		BackPathFinder backPathFinder = new BackPathFinder(loadedGraph);

		CandidatesPicker candidatesPicker = new CandidatesPicker(cands, startingNode, minLength);
		log.info("Choosing " + numOfTries + " candidates");
		List<Candidate> picked = candidatesPicker.selectCandidates(numOfTries);
		picked.forEach(c -> log.info(c.correspNode.getNode().getId()));

		List<TourEdge> foundPath;
		for (int i = 0; i < numOfTries; i++) {
			TreeNode randomCand = picked.get(i).correspNode;
			IOUtils.visualizePath(randomCand.pathFromRoot(), "forw_path_" + i + ".geojson");

			log.info("Finding back path from node " + randomCand.getNode().getId());
			foundPath = backPathFinder.getPathBack(picked.get(i), startingNode, maxLength, minLength);

			if (foundPath != null) {
				IOUtils.visualizeEdges(foundPath, "bestWalk_" + i + ".geojson");
			} else {
				log.info("Walk not found");
			}
		}
	}
}
