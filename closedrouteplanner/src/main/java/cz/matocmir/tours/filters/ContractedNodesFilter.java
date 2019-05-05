package cz.matocmir.tours.filters;

import cz.matocmir.tours.model.Candidate;
import cz.matocmir.tours.model.TourEdge;
import cz.matocmir.tours.model.TourGraph;
import cz.matocmir.tours.utils.IOUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ContractedNodesFilter implements CandidateFilter{
	private TourGraph graph;

	public ContractedNodesFilter(TourGraph graph) {
		this.graph = graph;
	}

	@Override
	public List<Candidate> filter(List<Candidate> cands) {
		List<Candidate> filtered = new ArrayList<>();
		Set<Integer> candIds = cands.stream().map(c -> c.correspNode.getNode().getId()).collect(Collectors.toSet());

		for(Candidate c : cands){
			Set<Integer> childs = new TreeSet<>();
			childs.addAll(graph.getOutEdges(c.correspNode.getNode().getId()).stream().map(TourEdge::getToId).collect(Collectors.toList()));
			childs.addAll(graph.getInEdges(c.correspNode.getNode().getId()).stream().map(TourEdge::getFromId).collect(Collectors.toList()));

			if(candIds.containsAll(childs)){
				candIds.remove(c.correspNode.getNode().getId());
				continue;
			}

			filtered.add(c);
		}

		System.out.println((cands.size()-filtered.size()) + " removed after first step (Contraction)");
		IOUtils.visualizeNodes(filtered.stream().map(e -> e.correspNode.getNode()).collect(Collectors.toList()), "filter3.geojson");
		return filtered;
	}
}
