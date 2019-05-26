package cz.matocmir.tours.filters;

import cz.matocmir.tours.model.Candidate;
import cz.matocmir.tours.model.TourNode;
import cz.matocmir.tours.utils.IOUtils;
import cz.matocmir.tours.utils.TourUtils;

import java.util.List;
import java.util.stream.Collectors;

public class Path2DistanceRatioFilter implements CandidateFilter {
	private double upperRatio;
	private double lowerRatio;
	private TourNode goal;

	public Path2DistanceRatioFilter(double upperRatio, double lowerRatio, TourNode goal){
		this.upperRatio = upperRatio;
		this.lowerRatio = lowerRatio;
		this.goal = goal;
	}

	@Override
	public List<Candidate> filter(List<Candidate> cands) {
		IOUtils.visualizeNodes(cands.stream().map(e -> e.correspNode.getNode()).collect(Collectors.toList()), "beforeRatio.geojson");
		List<Candidate> filtered = cands.stream().filter(this::inRatioRange).collect(Collectors.toList());
		IOUtils.visualizeNodes(filtered.stream().map(e -> e.correspNode.getNode()).collect(Collectors.toList()), "ratio.geojson");
		System.out.println((cands.size() - filtered.size()) + " after first step (Ratio)");
		return filtered;
	}

	private boolean inRatioRange(Candidate c) {
		double ratio = c.length/ TourUtils.computeGreatCircleDistance(c.correspNode.getNode(), goal);
		return (ratio > lowerRatio && ratio < upperRatio);
	}
}
