package cz.matocmir.tours;

import com.umotional.planningalgorithms.core.Dijkstra;
import com.umotional.planningalgorithms.core.ShortestPathAlgorithm;
import cz.matocmir.tours.model.*;
import cz.matocmir.tours.utils.IOUtils;
import cz.matocmir.tours.utils.TourUtils;
import cz.matocmir.tours.backpath.*;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

	public static void main(String[] args) {
		final String graphPath = "./src/main/resources/prague.csv";
		System.out.println("Loading graph from file: " + graphPath);
		TourGraph loadedGraph = IOUtils.loadGraph(graphPath);

		//System.out.println("Graph loaded ... dumping");
		//loadedGraph.dumpToGeoJson("test.geojson");

		TourNode startingNode = loadedGraph.getNode(17500);
		final int minLength = 4500;
		final int maxLength = 7000;

		List<Candidate> cands = forwardSearch(loadedGraph, startingNode, minLength, maxLength);
		System.out.println("Found: " + cands.size());
		IOUtils.visualizeNodes(cands.stream().map(c -> c.correspNode.getNode()).collect(Collectors.toList()), "found.geojson");

		int numOfTries = 11;
		CandidatesPicker candidatesPicker = new CandidatesPicker(cands, startingNode, minLength);
		List<Candidate> picked = candidatesPicker.selectCandidates(numOfTries);


		for(int i = 0;i<numOfTries;i++){
			TreeNode randomCand = picked.get(i).correspNode;
			IOUtils.visualizePath(randomCand.pathFromRoot(), "cand_path_" + i + ".geojson");
		}

		getPathBack(picked.get(0), startingNode, loadedGraph,maxLength);

		//TODO - backPath is now the same as forwardPath - take roundness into account
		//TODO - filter backPaths by length and cost
	}

	private static ArrayList<TourEdge> getPathBack(Candidate candidate,TourNode startingNode, TourGraph graph, double maxLength){
		ArrayList<TreeNode> forwardPath = candidate.correspNode.pathFromRoot();

		double forwardLength = 0;
		for(int i = 1;i<forwardPath.size();i++){
			forwardLength += forwardPath.get(i).getEdgeFromParent().getLengthInMeters();
			Candidate startCan = new Candidate(new TreeNode(null, null, forwardPath.get(i).getNode()), 0, 0);

			BackPathGoalChecker gc = new BackPathGoalChecker(startingNode);
			BackPathLabelFactory lf = new BackPathLabelFactory(graph, forwardLength,maxLength,startingNode);
			BackPathLabel start = new BackPathLabel(forwardPath.get(i).getNode().getId(), new int[]{0}, null, startCan);

			ShortestPathAlgorithm<BackPathLabel, BackPath> alg = new Dijkstra(lf, start,
					gc, new BackPathFactory());
			alg.call();
			List<TreeNode> res = gc.getResult();
			IOUtils.visualizePath(res, "back_" + i + ".geojson");
		}

		return null;
	}

	private static ArrayList<Candidate> forwardSearch(TourGraph graph, TourNode startNode, int minLength,
			int maxLength) {
		final double epsilon = 0.001;
		ArrayList<Candidate> candidates = new ArrayList<>();

		Queue<Candidate> q = new PriorityQueue<>(Comparator.comparingDouble(o -> o.weight));
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
							cur.length + e.getLengthInMeters());
					List<TourEdge> cnOutEdges = graph.getOutEdges(cn.correspNode.getNode().getId());
					HashSet<Integer> encounteredIds = new HashSet<>(); // Cycle detection


					while (cnOutEdges.size() == 1 && cn.length <= maxLength + epsilon) {
						e = cnOutEdges.get(0);
						cn = new Candidate(new TreeNode(e, cn.correspNode, e.getTo()), cn.weight + e.getCost(),
								cn.length + e.getLengthInMeters());
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
