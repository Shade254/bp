package cz.matocmir.tours.forwardpath;

import cz.matocmir.tours.model.*;
import cz.matocmir.tours.utils.TourUtils;
import org.apache.log4j.Logger;

import java.util.*;

public class CandidateFinder {
	private final Logger log = Logger.getLogger(CandidateFinder.class);
	private final double epsilon = 0.001;
	private TourGraph graph;

	public CandidateFinder(TourGraph graph) {
		this.graph = graph;
	}

	public ArrayList<Candidate> forwardSearch(TourNode startNode, int minLength, int maxLength) {
		ArrayList<Candidate> candidates = new ArrayList<>();

		Queue<Candidate> q = new PriorityQueue<>(Comparator.comparingDouble(o -> o.weight));
		q.add(new Candidate(new TreeNode(null, null, startNode), 0, 0));
		HashSet<Integer> addedIds = new HashSet<>();

		while (!q.isEmpty()) {
			Candidate cur = q.poll();
			TreeNode curN = cur.correspNode;
			if (addedIds.add(curN.getNode().getId())) {
				log.debug("Candidate " + cur.toString());
				boolean boundaryNode = false;

				List<TourEdge> outEdges = graph.getOutEdges(curN.getNode().getId());
				for (TourEdge e : outEdges) {
					log.debug("Edge " + e.toString());
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
							.computeGreatCircleDistance(cn.correspNode.getNode().getLatitude(),
									cn.correspNode.getNode().getLongitude(), startNode.getLatitude(),
									startNode.getLongitude());
					if (tourL <= maxLength + epsilon) {
						log.debug("Adding " + cn.toString() + " " + TourUtils
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
				double distance = TourUtils
						.computeGreatCircleDistance(curN.getNode().getLatitude(), curN.getNode().getLongitude(),
								startNode.getLatitude(), startNode.getLongitude());
				if (boundaryNode || minlenMinCurlen < 0 || distance + epsilon >= minlenMinCurlen) {
					candidates.add(cur);
				}
			}
		}
		return candidates;
	}
}
