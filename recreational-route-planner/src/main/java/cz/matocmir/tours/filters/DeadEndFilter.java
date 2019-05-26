package cz.matocmir.tours.filters;

import cz.matocmir.tours.model.Candidate;
import cz.matocmir.tours.model.TourEdge;
import cz.matocmir.tours.model.TourGraph;
import cz.matocmir.tours.utils.IOUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DeadEndFilter implements CandidateFilter {
	private TourGraph graph;

	public DeadEndFilter(TourGraph graph) {
		this.graph = graph;
	}

	@Override
	public List<Candidate> filter(List<Candidate> cands) {
		return filterDeadEnds(cands, graph);
	}

	public List<Candidate> filterDeadEnds(List<Candidate> unfiltred, TourGraph graph) {
		//IOUtils.visualizeNodes(unfiltred.stream().map(e -> e.correspNode.getNode()).collect(Collectors.toList()), "original.geojson");
		List<Candidate> filtered = new ArrayList<>();
		List<Candidate> removed = new ArrayList<>();
		for (Candidate c : unfiltred) {
			List<TourEdge> out = graph.getOutEdges(c.correspNode.getNode().getId());
			List<TourEdge> in = graph.getInEdges(c.correspNode.getNode().getId());

			if (out.size() < 1 || in.size() < 1) {
				removed.add(c);
				continue;
			}

			if (out.size() == 1 && in.size() == 1 && out.get(0).getToId() == in.get(0).getFromId()) {
				removed.add(c);
				continue;
			}

			filtered.add(c);
		}

		System.out.println(removed.size() + " removed after first step (Dead ends)");

		//IOUtils.visualizeNodes(filtered.stream().map(e -> e.correspNode.getNode()).collect(Collectors.toList()), "dead1.geojson");
		return secondStep(filtered, removed);
	}

	private List<Candidate> secondStep(List<Candidate> filtered, List<Candidate> removed) {
		Set<Integer> removedIds = removed.stream().map(e -> e.correspNode.getNode().getId()).collect(Collectors.toSet());
		Set<Integer> validIds = filtered.stream().map(e -> e.correspNode.getNode().getId()).collect(Collectors.toSet());
		List<Candidate> secondFiltered = new ArrayList<>();

		for(Candidate c : filtered){
			Set<Integer> surroundings = new HashSet<>();
			surroundings.addAll(graph.getOutEdges(c.correspNode.getNode()).stream().map(TourEdge::getToId).collect(Collectors.toList()));
			surroundings.addAll(graph.getInEdges(c.correspNode.getNode()).stream().map(TourEdge::getFromId).collect(Collectors.toList()));
			surroundings.removeAll(removedIds);
			if(surroundings.size()==1 && validIds.containsAll(surroundings)){
				removedIds.add(c.correspNode.getNode().getId());
				continue;
			}

			secondFiltered.add(c);

		}

		System.out.println(removedIds.size() + " removed after second step (Dead ends)");
		//IOUtils.visualizeNodes(secondFiltered.stream().map(e -> e.correspNode.getNode()).collect(Collectors.toList()), "dead2.geojson");
		return secondFiltered;
	}
}
