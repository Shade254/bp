package cz.matocmir.tours.filters;

import cz.matocmir.tours.model.Candidate;
import cz.matocmir.tours.model.TourEdge;
import cz.matocmir.tours.model.TourGraph;

import java.util.ArrayList;
import java.util.List;

public class DeadEndFilter implements CandidateFilter {
	private TourGraph graph;

	public DeadEndFilter(TourGraph graph) {
		this.graph = graph;
	}

	@Override
	public List<Candidate> filter(List<Candidate> cands) {
		return filterDeadEnds(cands, graph);
	}

	public static List<Candidate> filterDeadEnds(List<Candidate> unfiltred, TourGraph graph) {
		List<Candidate> filtered = new ArrayList<>();
		for (Candidate c : unfiltred) {
			List<TourEdge> out = graph.getOutEdges(c.correspNode.getNode().getId());
			List<TourEdge> in = graph.getInEdges(c.correspNode.getNode().getId());

			if (out.size() < 1 || in.size() < 1) {
				continue;
			}

			if (out.size() == 1 && in.size() == 1 && out.get(0).getToId() == in.get(0).getFromId()) {
				continue;
			}

			filtered.add(c);
		}
		return filtered;
	}
}
